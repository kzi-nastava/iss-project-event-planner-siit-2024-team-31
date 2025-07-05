package com.example.eventplanner.service;

import com.example.eventplanner.dto.eventDto.eventType.EventTypeDTO;
import com.example.eventplanner.dto.service.CreateServiceRequestDTO;
import com.example.eventplanner.dto.service.ProvidedServiceDTO;
import com.example.eventplanner.dto.service.UpdateProvidedServiceRequestDTO;
import com.example.eventplanner.dto.service_category.ProvidedServiceCategoryDTO;
import com.example.eventplanner.exception.exceptions.eventType.EventTypeNotFoundException;
import com.example.eventplanner.exception.exceptions.user.UserNotFoundException;
import com.example.eventplanner.model.EntityBase;
import com.example.eventplanner.model.ItemPhoto;
import com.example.eventplanner.model.Status;
import com.example.eventplanner.model.service.ProvidedService;
import com.example.eventplanner.model.service.ProvidedServiceCategory;
import com.example.eventplanner.repository.*;
import com.example.eventplanner.utils.types.ProvidedServiceFilterCriteria;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.persistence.criteria.Predicate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public ProvidedService create(CreateServiceRequestDTO dto, String pupEmail) throws UserNotFoundException {

        var pup = userRepository.findByEmail(pupEmail)
                .orElseThrow(() -> new UserNotFoundException("User " + pupEmail + " not found"));

        ProvidedService newService = new ProvidedService();

        //Category
        Optional<ProvidedServiceCategory> catOpt = providedServiceRepository.findByName(dto.getCategory());

        if (catOpt.isEmpty()) {
            Status pending = statusRepository.findByName("PENDING");
            ProvidedServiceCategory cat = new ProvidedServiceCategory();
            cat.setName(dto.getCategory());
            cat.setDescription("");
            cat.setStatus(pending);
            newService.setStatus(pending);
        }
        else {
            newService.setCategory(catOpt.get());
            newService.setStatus(statusRepository.findByName("ACTIVE"));
        }

        newService.setPup(pup);
        newService.setName(dto.getName());
        newService.setDescription(dto.getDescription());
        newService.setPeculiarities(dto.getPeculiarities());
        newService.setPrice(dto.getPrice());
        newService.setDiscount(dto.getDiscount());


        newService.setAvailable(dto.getIsAvailable());
        newService.setVisible(dto.getIsVisible());

        //Timings
        if (Boolean.TRUE.equals(dto.getNoTimeSelectionRequired())) {
            newService.setTimeManagement(false);
            newService.setServiceDurationMinMinutes(null);
            newService.setServiceDurationMaxMinutes(null);
        } else if (Boolean.TRUE.equals(dto.getManualTimeSelection())) {
            newService.setTimeManagement(true);
            newService.setServiceDurationMinMinutes(dto.getServiceDurationMin());
            newService.setServiceDurationMaxMinutes(dto.getServiceDurationMax());
        } else {
            newService.setTimeManagement(false);
            newService.setServiceDurationMinMinutes(dto.getServiceDurationMin());
            newService.setServiceDurationMaxMinutes(null);
        }

        newService.setBookingConfirmation("manual".equalsIgnoreCase(dto.getBookingConfirmation()));
        newService.setBookingDeclineDeadlineHours(dto.getBookingDeclineDeadline());

        //Event types
        newService.setSuitableEventTypes(
                dto.getSuitableEventTypes().stream()
                        .map(id -> eventTypesRepository.findById(id)
                                .orElseThrow(() -> new EventTypeNotFoundException("Event type with id " + id + " not found")))
                        .collect(Collectors.toList())
        );

        //Photos
        if (dto.getPhotos() != null) {
            List<String> urls = photoService.uploadPhotos(dto.getPhotos(), bucketName, "service-photos");
            urls.forEach(url -> {
                ItemPhoto p = new ItemPhoto();
                p.setPhotoUrl(url);
                p.setService(newService);
                newService.getPhotos().add(p);
            });
        }

        return providedServiceRepository.save(newService);
    }

    public void update(Long serviceId, UpdateProvidedServiceRequestDTO dto, String pupEmail) {
        var pup = userRepository.findByEmail(pupEmail)
                .orElseThrow(() -> new UserNotFoundException("User " + pupEmail + " not found"));

        var service = providedServiceRepository.findById(serviceId)
                .orElseThrow(() -> new IllegalArgumentException("Service with id " + serviceId + " not found"));

        if (!service.getPup().getId().equals(pup.getId())) {
            throw new UserNotFoundException("Service with id " + serviceId + " does not belong to user " + pupEmail);
        }

        //TODO STUDENT 2: For add check if service is reserved
        //and if so, throw exception or store previous version of service
        //until reservation is completed or cancelled

        service.setVersion(service.getVersion() + 1);
        service.setName(dto.getName());
        service.setDescription(dto.getDescription());
        service.setPeculiarities(dto.getPeculiarities());
        service.setPrice(dto.getPrice());
        service.setDiscount(dto.getDiscount());
        service.setAvailable(dto.getIsAvailable());
        service.setVisible(dto.getIsVisible());

        service.setBookingDeclineDeadlineHours(dto.getBookingDeclineDeadlineHours());
        service.setServiceDurationMaxMinutes(dto.getServiceDurationMaxMinutes());
        service.setServiceDurationMinMinutes(dto.getServiceDurationMinMinutes());

        //Photos
        if (dto.getPhotos() != null) {
            List<String> urls = photoService.uploadPhotos(dto.getPhotos(), bucketName, "service-photos");
            urls.forEach(url -> {
                ItemPhoto p = new ItemPhoto();
                p.setPhotoUrl(url);
                p.setService(service);
                service.getPhotos().add(p);
            });
        }

        if (dto.getDeletedPhotosIds() != null) {
            dto.getDeletedPhotosIds().forEach(photoId -> {
                ItemPhoto photo = service.getPhotos().stream()
                        .filter(p -> p.getId().equals(photoId))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("Photo with id " + photoId + " not found"));
                photoService.deletePhotoByKey(photo.getPhotoUrl(), bucketName);
                service.getPhotos().remove(photo);
            });
        }

        //Suitable event types
        if (dto.getSuitableEventTypes() != null) {
            service.setSuitableEventTypes(
                    dto.getSuitableEventTypes().stream()
                            .map(id -> eventTypesRepository.findById(id)
                                    .orElseThrow(() -> new EventTypeNotFoundException("Event type with id " + id + " not found")))
                            .collect(Collectors.toList())
            );
        }

        providedServiceRepository.save(service);

    }

    public void delete(Long serviceId, String pupEmail) {
        var pup = userRepository.findByEmail(pupEmail)
                .orElseThrow(() -> new UserNotFoundException("User " + pupEmail + " not found"));

        ProvidedService service = providedServiceRepository.findById(serviceId)
                .orElseThrow(() -> new IllegalArgumentException("Service with id " + serviceId + " not found"));

        if (!service.getPup().getId().equals(pup.getId())) {
            throw new UserNotFoundException("Service with id " + serviceId + " does not belong to user " + pupEmail);
        }

        service.setDeleted(true);
        providedServiceRepository.save(service);
    }

    public ProvidedServiceDTO getProvidedServiceDataForProviderById(Long serviceId, String pupEmail) {
        var pup = userRepository.findByEmail(pupEmail)
                .orElseThrow(() -> new UserNotFoundException("User " + pupEmail + " not found"));

        ProvidedService service = providedServiceRepository.findById(serviceId)
                .orElseThrow(() -> new IllegalArgumentException("Service with id " + serviceId + " not found"));

        if (!service.getPup().getId().equals(pup.getId())) {
            throw new UserNotFoundException("Service with id " + serviceId + " does not belong to user " + pupEmail);
        }

        return providedServiceToProvidedServiceDTO(service);
    }

    public List<ProvidedServiceDTO> getTop5Services() {
        return providedServiceRepository
                .findTop5ByOrderByRatingDesc()
                .stream()
                .map(this::providedServiceToProvidedServiceDTO)
                .toList();
    }

    public Page<ProvidedServiceDTO> searchServices(String keyword, Pageable pg) {
        Page<ProvidedService> page = (keyword == null || keyword.isBlank())
                ? providedServiceRepository.findAll(pg)
                : providedServiceRepository
                .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(keyword, keyword, pg);
        return page.map(this::providedServiceToProvidedServiceDTO);
    }

    public Page<ProvidedServiceDTO> filterSearchServices(ProvidedServiceFilterCriteria c, Pageable pg) {
        Specification<ProvidedService> spec = Specification.where(null);

        if (c.getKeyword() != null && !c.getKeyword().isBlank()) {
            String kw = "%" + c.getKeyword().toLowerCase() + "%";
            spec = spec.and((r, q, cb) -> cb.or(
                    cb.like(cb.lower(r.get("name")), kw),
                    cb.like(cb.lower(r.get("description")), kw)
            ));
        }

        if (c.getCategoryIds() != null && !c.getCategoryIds().isEmpty()) {
            spec = spec.and((r, q, cb) ->
                    r.get("category").get("id").in(c.getCategoryIds()));
        }
        if (c.getMinPrice() != null) {
            spec = spec.and((r, q, cb) ->
                    cb.ge(r.get("price"), c.getMinPrice()));
        }
        if (c.getMaxPrice() != null) {
            spec = spec.and((r, q, cb) ->
                    cb.le(r.get("price"), c.getMaxPrice()));
        }
        if (c.getMinRating() != null) {
            spec = spec.and((r, q, cb) ->
                    cb.ge(r.get("rating"), c.getMinRating()));
        }
        if (c.getIsAvailable() != null) {
            spec = spec.and((r, q, cb) ->
                    cb.equal(r.get("isAvailable"), c.getIsAvailable()));
        }
        if (c.getPupId() != null) {
            spec = spec.and((r, q, cb) ->
                    cb.equal(r.get("pup").get("id"), Long.parseLong(c.getPupId())));
        }

        // Фильтр по дате доступности
        if (c.getAvailableFrom() != null) {
            spec = spec.and((r, q, cb) ->
                    cb.greaterThanOrEqualTo(r.get("availableFrom"), c.getAvailableFrom()));
        }
        if (c.getAvailableTo() != null) {
            spec = spec.and((r, q, cb) ->
                    cb.lessThanOrEqualTo(r.get("availableTo"), c.getAvailableTo()));
        }

        if (c.getServiceDurationMinMinutes() != null) {
            Integer minMin = c.getServiceDurationMinMinutes();
            spec = spec.and((r, q, cb) -> {
                Predicate noMax  = cb.isNull(r.get("serviceDurationMaxMinutes"));
                Predicate enough = cb.ge(r.get("serviceDurationMaxMinutes"), minMin);
                return cb.or(noMax, enough);
            });
        }
        if (c.getServiceDurationMaxMinutes() != null) {
            Integer maxMin = c.getServiceDurationMaxMinutes();
            spec = spec.and((r, q, cb) ->
                    cb.le(r.get("serviceDurationMinMinutes"), maxMin));
        }

        // suitableFor filtering using an explicit join
        if (c.getSuitableFor() != null && !c.getSuitableFor().isEmpty()) {
            spec = spec.and((root, query, cb) -> {
                query.distinct(true);
                var join = root.join("suitableEventTypes");
                return join.get("id").in(c.getSuitableFor());
            });
        }

        return providedServiceRepository
                .findAll(spec, pg)
                .map(this::providedServiceToProvidedServiceDTO);
    }

    public ProvidedServiceFilterCriteria getFilterOptions() {
        List<Long> catIds = providedServiceCategoryRepository.findAll()
                .stream().map(ProvidedServiceCategory::getId).toList();

        Double minP = providedServiceRepository.findAll()
                .stream().map(ProvidedService::getPrice)
                .min(Double::compareTo).orElse(0.0);
        Double maxP = providedServiceRepository.findAll()
                .stream().map(ProvidedService::getPrice)
                .max(Double::compareTo).orElse(0.0);

        Integer minDuration = providedServiceRepository.findAll()
                .stream().map(ProvidedService::getServiceDurationMinMinutes)
                .filter(Objects::nonNull)
                .min(Integer::compareTo).orElse(0);

        Integer maxDuration = providedServiceRepository.findAll()
                .stream().map(ProvidedService::getServiceDurationMaxMinutes)
                .filter(Objects::nonNull)
                .max(Integer::compareTo).orElse(null);

        return ProvidedServiceFilterCriteria.builder()
                .categoryIds(catIds)
                .minPrice(minP)
                .maxPrice(maxP)
                .minRating(0.0)
                .serviceDurationMinMinutes(minDuration)
                .serviceDurationMaxMinutes(maxDuration)
                .build();
    }

    public Page<ProvidedServiceDTO> searchMyServices(String pupEmail, Pageable pageable) {
        var pup = userRepository.findByEmail(pupEmail)
                .orElseThrow(() -> new UserNotFoundException("User " + pupEmail + " not found"));
        return providedServiceRepository
                .findAllByPup(pup, pageable)
                .map(this::providedServiceToProvidedServiceDTO);
    }

    public ProvidedServiceDTO providedServiceToProvidedServiceDTO(ProvidedService svc) {
        ProvidedServiceDTO d = new ProvidedServiceDTO();

        ProvidedServiceCategoryDTO catDto = new ProvidedServiceCategoryDTO();
        catDto.setId(svc.getCategory().getId());
        catDto.setName(svc.getCategory().getName());
        catDto.setDescription(svc.getCategory().getDescription());
        catDto.setStatus(svc.getCategory().getStatus().getName());

        d.setCategory(catDto);
        d.setId(svc.getId());
        d.setPupId(svc.getPup().getId());
        d.setName(svc.getName());
        d.setDescription(svc.getDescription());
        d.setPeculiarities(svc.getPeculiarities());
        d.setPrice(svc.getPrice());
        d.setDiscount(svc.getDiscount());
        d.setSuitableEventTypes(svc.getSuitableEventTypes().stream()
        .map(et -> {
                    EventTypeDTO etDto = new EventTypeDTO();
                    etDto.setId(et.getId());
                    etDto.setName(et.getName());
                    etDto.setDescription(et.getDescription());
                    return etDto;
                }).collect(Collectors.toList()));
        d.setVisible(svc.isVisible());
        d.setAvailable(svc.isAvailable());
        d.setRating(svc.getRating());
        d.setBookingConfirmation(svc.getBookingConfirmation());
        d.setBookingDeclineDeadlineHours(svc.getBookingDeclineDeadlineHours());
        d.setTimeManagement(svc.getTimeManagement());
        d.setServiceDurationMinMinutes(svc.getServiceDurationMinMinutes());
        d.setServiceDurationMaxMinutes(svc.getServiceDurationMaxMinutes());
        d.setStatus(svc.getStatus());
        return d;
    }


}