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
        Status pending = statusRepository.findByName("PENDING");
        var pup = userRepository.findByEmail(pupEmail)
                .orElseThrow(() -> new UserNotFoundException("User " + pupEmail + " not found"));

        ProvidedService svc = new ProvidedService();
        svc.setPup(pup);
        svc.setName(dto.getName());
        svc.setDescription(dto.getDescription());
        svc.setPeculiarities(dto.getPeculiarities());
        svc.setPrice(dto.getPrice());
        svc.setDiscount(dto.getDiscount());

        // категория
        Optional<ProvidedServiceCategory> catOpt = providedServiceRepository.findByName(dto.getCategory());
        ProvidedServiceCategory cat = catOpt
                .orElseGet(() -> {
                    ProvidedServiceCategory c = new ProvidedServiceCategory();
                    c.setName(dto.getCategory());
                    c.setDescription("");
                    c.setStatus(pending);
                    return providedServiceCategoryRepository.saveAndFlush(c);
                });
        svc.setCategory(cat);
        svc.setAvailable(Boolean.TRUE.equals(dto.getIsAvailable()));
        svc.setVisible(Boolean.TRUE.equals(dto.getIsVisible()));

        // время
        if (Boolean.TRUE.equals(dto.getNoTimeSelectionRequired())) {
            svc.setTimeManagement(false);
            svc.setServiceDurationMinMinutes(null);
            svc.setServiceDurationMaxMinutes(null);
        } else if (Boolean.TRUE.equals(dto.getManualTimeSelection())) {
            svc.setTimeManagement(true);
            svc.setServiceDurationMinMinutes(dto.getServiceDurationMin());
            svc.setServiceDurationMaxMinutes(dto.getServiceDurationMax());
        } else {
            svc.setTimeManagement(false);
            svc.setServiceDurationMinMinutes(dto.getServiceDurationMin());
            svc.setServiceDurationMaxMinutes(null);
        }

        svc.setBookingConfirmation("manual".equalsIgnoreCase(dto.getBookingConfirmation()));
        svc.setBookingDeclineDeadlineHours(dto.getBookingDeclineDeadline());

        // связь с EventTypes
        eventTypesRepository.findAllById(dto.getSuitableEventTypes()).forEach(et -> {
            var rec = et.getRecommendedProvidedServiceCategories();
            if (!rec.contains(cat)) {
                rec.add(cat);
                et.setRecommendedProvidedServiceCategories(rec);
                eventTypesRepository.saveAndFlush(et);
            }
        });

        // фото
        if (dto.getPhotos() != null) {
            List<String> urls = photoService.uploadPhotos(dto.getPhotos(), bucketName, "service-photos");
            urls.forEach(url -> {
                ItemPhoto p = new ItemPhoto();
                p.setPhotoUrl(url);
                p.setService(svc);
                svc.getPhotos().add(p);
            });
        }

        return providedServiceRepository.save(svc);
    }

    public List<ProvidedServiceDTO> getTop5Services() {
        return providedServiceRepository
                .findTop5ByOrderByRatingDesc()
                .stream()
                .map(this::toDto)
                .toList();
    }

    public Page<ProvidedServiceDTO> searchServices(String keyword, Pageable pg) {
        Page<ProvidedService> page = (keyword == null || keyword.isBlank())
                ? providedServiceRepository.findAll(pg)
                : providedServiceRepository
                .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(keyword, keyword, pg);
        return page.map(this::toDto);
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
                .map(this::toDto);
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
                .map(this::toDto);
    }

    private ProvidedServiceDTO toDto(ProvidedService svc) {
        ProvidedServiceDTO d = new ProvidedServiceDTO();
        d.setId(svc.getId());
        d.setPupId(svc.getPup().getId());
        d.setName(svc.getName());
        d.setDescription(svc.getDescription());
        d.setPeculiarities(svc.getPeculiarities());
        d.setPrice(svc.getPrice());
        d.setDiscount(svc.getDiscount());
        d.setPhotos(svc.getPhotos().stream()
                .map(ItemPhoto::getPhotoUrl).toList());
        d.setSuitableEventTypes(svc.getSuitableEventTypes()
                .stream().map(EntityBase::getId).toList());
        d.setVisible(svc.isVisible());
        d.setAvailable(svc.isAvailable());
        d.setRating(svc.getRating());
        d.setBookingConfirmation(svc.getBookingConfirmation());
        d.setBookingDeclineDeadlineHours(svc.getBookingDeclineDeadlineHours());
        d.setTimeManagement(svc.getTimeManagement());
        d.setServiceDurationMinMinutes(svc.getServiceDurationMinMinutes());
        d.setServiceDurationMaxMinutes(svc.getServiceDurationMaxMinutes());
        return d;
    }


}