package com.example.eventplanner.service;

import com.example.eventplanner.dto.service.CreateServiceRequestDTO;
import com.example.eventplanner.exception.exceptions.user.UserNotFoundException;
import com.example.eventplanner.model.ItemPhoto;
import com.example.eventplanner.model.Status;
import com.example.eventplanner.model.service.ProvidedService;
import com.example.eventplanner.model.service.ProvidedServiceCategory;
import com.example.eventplanner.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
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

}
