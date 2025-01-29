package com.example.eventplanner.service;

import com.example.eventplanner.dto.product.ProductCategoryDTO;
import com.example.eventplanner.dto.product.CreateProductRequestDTO;
import com.example.eventplanner.dto.product.ProductDto;
import com.example.eventplanner.exception.UserNotFoundException;
import com.example.eventplanner.model.ProductPhoto;
import com.example.eventplanner.model.Status;
import com.example.eventplanner.model.product.ProductCategory;
import com.example.eventplanner.model.product.Product;
import com.example.eventplanner.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Категорию продукта (из выбранных категорий PUP-а, на которые он зарегистрирован).
// Если подходящей категории для продукта нет,
// PUP может предложить новую категорию при создании продукта.
// В этом случае при нажатии на кнопку «Отправить» продукт не создается сразу,
// а переводится в статус «Ожидание», и администратору отправляется уведомление о предложенной категории.
// Название, описание, цену, скидку и изображения.
// Типы событий, к которым продукт привязан (0 или более).
// Видимость (доступность продукта для OD) и доступность продукта для покупки конечными пользователями


@Service
@RequiredArgsConstructor
public class ProductService {

       private final ProductRepository productRepository;
       private final ProductCategoryRepository categoryRepository;
       private final EventTypesRepository eventTypesRepository;
       private final UserRepository userRepository;
       private final PhotoService photoService;
       private final StatusRepository statusRepository;

       @Value("${aws.s3.bucket-name}")
       private String bucketName;

    public Product create(CreateProductRequestDTO createProductRequestDTO, String pupEmail) throws UserNotFoundException {
        final Status pendingStatus = statusRepository.findByName("PENDING");
        var pup = userRepository.findByEmail(pupEmail).orElseThrow(() -> new UserNotFoundException(pupEmail));

        Product product = new Product();
        product.setPup(pup);
        product.setName(createProductRequestDTO.getName());
        product.setDescription(createProductRequestDTO.getDescription());
        product.setPeculiarities(createProductRequestDTO.getPeculiarities());
        product.setPrice(createProductRequestDTO.getPrice());
        product.setDiscount(createProductRequestDTO.getDiscount());

        // Handle category assignment
        ProductCategory category;
        Optional<ProductCategory> categoryOpt = categoryRepository.findByName(createProductRequestDTO.getCategory());
        if (categoryOpt.isPresent()) {
            category = categoryOpt.get();
            product.setCategory(category);
            product.setAvailable(Boolean.TRUE.equals(createProductRequestDTO.getIsAvailable()));
            product.setVisible(Boolean.TRUE.equals(createProductRequestDTO.getIsVisible()));
        } else {
            // Создаём новую категорию
            ProductCategory newCategory = new ProductCategory();
            newCategory.setName(createProductRequestDTO.getCategory());
            newCategory.setDescription(""); // Admin will update the description
            newCategory.setStatus(pendingStatus);
            newCategory = categoryRepository.saveAndFlush(newCategory); // Сохраняем и обновляем объект

            product.setCategory(newCategory);
            product.setVisible(false);
            product.setAvailable(false);

            category = newCategory; // Назначаем новую категорию переменной
        }

        // Handle time management and booking rules
        if (Boolean.TRUE.equals(createProductRequestDTO.getNoTimeSelectionRequired())) {
            product.setTimeManagement(false);
            product.setServiceDurationMinMinutes(null);
            product.setServiceDurationMaxMinutes(null);
        } else if (Boolean.TRUE.equals(createProductRequestDTO.getManualTimeSelection())) {
            product.setTimeManagement(true);
            product.setServiceDurationMinMinutes(createProductRequestDTO.getServiceDurationMin());
            product.setServiceDurationMaxMinutes(createProductRequestDTO.getServiceDurationMax());
        } else {
            product.setTimeManagement(false);
            product.setServiceDurationMinMinutes(createProductRequestDTO.getServiceDurationMin());
            product.setServiceDurationMaxMinutes(null);
        }

        String bookingConf = createProductRequestDTO.getBookingConfirmation();
        product.setBookingConfirmation("manual".equalsIgnoreCase(bookingConf));
        product.setBookingDeclineDeadlineHours(createProductRequestDTO.getBookingDeclineDeadline());

        // Assign connection between suitable event types and product categories
        List<Long> suitableEventTypes = createProductRequestDTO.getSuitableEventTypes();
        eventTypesRepository.findAllById(suitableEventTypes).forEach(eventType -> {
            List<ProductCategory> connectedCategories = eventType.getRecommendedCategories();
            if (!connectedCategories.contains(category)) {
                connectedCategories.add(category);
            }
            eventType.setRecommendedCategories(connectedCategories);
            eventTypesRepository.saveAndFlush(eventType);
        });

        product.setLikes(0L);

        // Handle product photos
        if (createProductRequestDTO.getPhotos() != null) {
            List<MultipartFile> photos = createProductRequestDTO.getPhotos();
            String photosPrefix = "products-photos";
            List<String> photoUrls = photoService.uploadPhotos(photos, bucketName, photosPrefix);

            for (String url : photoUrls) {
                ProductPhoto productPhoto = new ProductPhoto();
                productPhoto.setPhotoUrl(url);
                productPhoto.setProduct(product);
                product.getPhotos().add(productPhoto);
            }
        }

        return productRepository.save(product);
    }

//       public void update (ProductDto productDto) {
//           Product product = new Product();
//           productRepository.saveAndFlush(product);
//       }

        public List<ProductCategoryDTO> getAllCategories() {
           var allCategories = categoryRepository.findAll();
           List<ProductCategoryDTO> categories = new ArrayList<>();
           allCategories.forEach(category -> {
               ProductCategoryDTO categoryDTO = new ProductCategoryDTO();
               categoryDTO.setId(category.getId());
               categoryDTO.setName(category.getName());
               categories.add(categoryDTO);
           });
           return categories;
        }

       public List<ProductDto> findAll(PageRequest of) { return productRepository.findAll(of).stream().map(ProductDto::fromProduct).toList();}

       public List<ProductDto> findTopFive() { return productRepository.findAll().stream().map(ProductDto::fromProduct).toList();}

}
