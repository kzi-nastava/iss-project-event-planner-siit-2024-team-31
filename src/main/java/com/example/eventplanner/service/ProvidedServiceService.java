package com.example.eventplanner.service;

import com.example.eventplanner.dto.service.CreateServiceRequestDTO;
import com.example.eventplanner.dto.service.ProvidedServiceDTO;
import com.example.eventplanner.exception.exceptions.user.UserNotFoundException;
import com.example.eventplanner.model.EntityBase;
import com.example.eventplanner.model.ItemPhoto;
import com.example.eventplanner.model.Status;
import com.example.eventplanner.model.service.ProvidedService;
import com.example.eventplanner.model.service.ProvidedServiceCategory;
import com.example.eventplanner.repository.*;
import com.example.eventplanner.utils.types.ProductFilterCriteria;
import com.example.eventplanner.utils.types.ProvidedServiceFilterCriteria;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor

public class ProvidedServiceService {

    private final ProvidedServiceRepository providedServiceRepository;
    private final ProvidedServiceCategoryRepository providedServiceCategoryRepository;
    private final EventTypesRepository eventTypesRepository;
    private final UserRepository userRepository;
    private final PhotoService photoService;
    private final StatusRepository statusRepository;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    public ProvidedService create(CreateServiceRequestDTO createServiceRequestDTO, String pupEmail) throws UserNotFoundException {

        final Status pendingStatus = statusRepository.findByName("PENDING");
        var pup = userRepository.findByEmail(pupEmail).orElseThrow(() -> new UserNotFoundException("User with " + pupEmail + " not found"));

        ProvidedService providedService = new ProvidedService();
        providedService.setPup(pup);
        providedService.setName(createServiceRequestDTO.getName());
        providedService.setDescription(createServiceRequestDTO.getDescription());
        providedService.setPeculiarities(createServiceRequestDTO.getPeculiarities());
        providedService.setPrice(createServiceRequestDTO.getPrice());
        providedService.setDiscount(createServiceRequestDTO.getDiscount());

        // Handle category assignment
        ProvidedServiceCategory category;
        Optional<ProvidedServiceCategory> categoryOpt = providedServiceRepository.findByName(createServiceRequestDTO.getCategory());
        if (categoryOpt.isPresent()) {
            category = categoryOpt.get();
            providedService.setCategory(category);
            providedService.setAvailable(Boolean.TRUE.equals(createServiceRequestDTO.getIsAvailable()));
            providedService.setVisible(Boolean.TRUE.equals(createServiceRequestDTO.getIsVisible()));
        } else {
            ProvidedServiceCategory newCategory = new ProvidedServiceCategory();
            newCategory.setName(createServiceRequestDTO.getCategory());
            newCategory.setDescription(""); // Admin will update the description
            newCategory.setStatus(pendingStatus);
            newCategory = providedServiceCategoryRepository.saveAndFlush(newCategory);

            providedService.setCategory(newCategory);
            providedService.setVisible(false);
            providedService.setAvailable(false);

            category = newCategory;
        }

        // Handle time management and booking rules
        if (Boolean.TRUE.equals(createServiceRequestDTO.getNoTimeSelectionRequired())) {
            providedService.setTimeManagement(false);
            providedService.setServiceDurationMinMinutes(null);
            providedService.setServiceDurationMaxMinutes(null);
        } else if (Boolean.TRUE.equals(createServiceRequestDTO.getManualTimeSelection())) {
            providedService.setTimeManagement(true);
            providedService.setServiceDurationMinMinutes(createServiceRequestDTO.getServiceDurationMin());
            providedService.setServiceDurationMaxMinutes(createServiceRequestDTO.getServiceDurationMax());
        } else {
            providedService.setTimeManagement(false);
            providedService.setServiceDurationMinMinutes(createServiceRequestDTO.getServiceDurationMin());
            providedService.setServiceDurationMaxMinutes(null);
        }

        String bookingConf = createServiceRequestDTO.getBookingConfirmation();
        providedService.setBookingConfirmation("manual".equalsIgnoreCase(bookingConf));
        providedService.setBookingDeclineDeadlineHours(createServiceRequestDTO.getBookingDeclineDeadline());

        // Assign connection between suitable event types and product categories
        List<Long> suitableEventTypes = createServiceRequestDTO.getSuitableEventTypes();
        eventTypesRepository.findAllById(suitableEventTypes).forEach(eventType -> {
            List<ProvidedServiceCategory> connectedCategories = eventType.getRecommendedProvidedServiceCategories();
            if (!connectedCategories.contains(category)) {
                connectedCategories.add(category);
            }
            eventType.setRecommendedProvidedServiceCategories(connectedCategories);
            eventTypesRepository.saveAndFlush(eventType);
        });

        // Handle product photos
        if (createServiceRequestDTO.getPhotos() != null) {
            List<MultipartFile> photos = createServiceRequestDTO.getPhotos();
            String photosPrefix = "service-photos";
            List<String> photoUrls = photoService.uploadPhotos(photos, bucketName, photosPrefix);

            for (String url : photoUrls) {
                ItemPhoto productPhoto = new ItemPhoto();
                productPhoto.setPhotoUrl(url);
                productPhoto.setService(providedService);
                providedService.getPhotos().add(productPhoto);
            }
        }

        return providedServiceRepository.save(providedService);
    }

    public List<ProvidedServiceDTO> getTop5Services() {
        return providedServiceRepository
                .findTop5ByOrderByRatingDesc()
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    public Page<ProvidedServiceDTO> searchServices(String keyword, Pageable pageable) {
        Page<ProvidedService> servicesPage;
        if (keyword == null || keyword.isEmpty()) {
            servicesPage = providedServiceRepository.findAll(pageable);
        }
        else {
            servicesPage = providedServiceRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(keyword, keyword, pageable);
        }
        return servicesPage.map(this::convertToDTO);
    }

    public Page<ProvidedServiceDTO> filterSearchServices(
            ProvidedServiceFilterCriteria c,
            Pageable pageable) {

        Specification<ProvidedService> spec = Specification.where(null);

        if (c.getKeyword() != null && !c.getKeyword().isBlank()) {
            String kw = "%" + c.getKeyword().toLowerCase() + "%";
            spec = spec.and((r, q, cb) ->
                    cb.or(
                            cb.like(cb.lower(r.get("name")), kw),
                            cb.like(cb.lower(r.get("description")), kw)
                    )
            );
        }
        if (c.getCategoryIds() != null && !c.getCategoryIds().isEmpty()) {
            spec = spec.and((r, q, cb) ->
                    r.get("category").get("id").in(c.getCategoryIds())
            );
        }
        if (c.getMinPrice() != null) {
            spec = spec.and((r, q, cb) ->
                    cb.ge(r.get("price"), c.getMinPrice())
            );
        }
        if (c.getMaxPrice() != null) {
            spec = spec.and((r, q, cb) ->
                    cb.le(r.get("price"), c.getMaxPrice())
            );
        }
        if (c.getMinRating() != null) {
            spec = spec.and((r, q, cb) ->
                    cb.ge(r.get("rating"), c.getMinRating())
            );
        }
        if (c.getIsAvailable() != null) {
            spec = spec.and((r, q, cb) ->
                    cb.equal(r.get("isAvailable"), c.getIsAvailable())
            );
        }
        if (c.getPupId() != null) {
            spec = spec.and((r, q, cb) ->
                    cb.equal(r.get("pup").get("id"),
                            Long.parseLong(c.getPupId()))
            );
        }

        return providedServiceRepository
                .findAll(spec, pageable)
                .map(this::convertToDTO);
    }

    public ProvidedServiceFilterCriteria getFilterOptions() {

        List<Long> categoryIds = providedServiceCategoryRepository
                .findAll().stream()
                .map(ProvidedServiceCategory::getId)
                .toList();


        Double minPrice = providedServiceRepository.findAll().stream()
                .map(ProvidedService::getPrice)
                .min(Comparator.naturalOrder())
                .orElse(0.0);
        Double maxPrice = providedServiceRepository.findAll().stream()
                .map(ProvidedService::getPrice)
                .max(Double::compareTo)
                .orElse(0.0);

        return ProvidedServiceFilterCriteria.builder()
                .categoryIds(categoryIds)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .minRating(0.0)
                .isAvailable(null)
                .build();
    }

    // Helper method to convert ProvidedService to ProvidedServiceDTO
    private ProvidedServiceDTO convertToDTO(com.example.eventplanner.model.service.ProvidedService providedService) {
        ProvidedServiceDTO dto = new ProvidedServiceDTO();
        dto.setId(providedService.getId());
        dto.setPupId(providedService.getPup().getId());
        dto.setName(providedService.getName());
        dto.setDescription(providedService.getDescription());
        dto.setPeculiarities(providedService.getPeculiarities());
        dto.setPrice(providedService.getPrice());
        dto.setDiscount(providedService.getDiscount());
        dto.setPhotos(providedService.getPhotos().stream().map(ItemPhoto::getPhotoUrl).toList());
        dto.setSuitableEventTypes(providedService.getSuitableEventTypes().stream().map(EntityBase::getId).toList());
        dto.setVisible(providedService.isVisible());
        dto.setAvailable(providedService.isAvailable());
        dto.setRating(providedService.getRating());
        dto.setBookingConfirmation(providedService.getBookingConfirmation());
        dto.setBookingDeclineDeadlineHours(providedService.getBookingDeclineDeadlineHours());
        dto.setTimeManagement(providedService.getTimeManagement());
        dto.setServiceDurationMinMinutes(providedService.getServiceDurationMinMinutes());
        dto.setServiceDurationMaxMinutes(providedService.getServiceDurationMaxMinutes());
        return dto;
    }


}
