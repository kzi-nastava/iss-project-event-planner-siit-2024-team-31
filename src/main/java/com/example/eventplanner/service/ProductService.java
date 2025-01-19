package com.example.eventplanner.service;

import com.example.eventplanner.dto.product.CreateProductRequestDTO;
import com.example.eventplanner.dto.product.ProductDto;
import com.example.eventplanner.exception.EventNotFoundException;
import com.example.eventplanner.exception.UserNotFoundException;
import com.example.eventplanner.model.EventTypeProductLink;
import com.example.eventplanner.model.ProductPhoto;
import com.example.eventplanner.model.Status;
import com.example.eventplanner.model.event.EventType;
import com.example.eventplanner.model.product.Category;
import com.example.eventplanner.model.product.Product;
import com.example.eventplanner.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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

            Optional<Category> categoryOpt = categoryRepository.findByName(createProductRequestDTO.getCategory());


            if (categoryOpt.isPresent()) {

                //if category presented just add it to product
                product.setCategory(categoryOpt.get());
                product.setAvailable(Boolean.TRUE.equals(createProductRequestDTO.getIsAvailable()));
                product.setVisible(Boolean.TRUE.equals(createProductRequestDTO.getIsVisible()));

            } else {

                //else create new category with PENDING status
                Category newCategory = new Category();
                newCategory.setName(createProductRequestDTO.getCategory());
                newCategory.setDescription(""); // description adds admin when approve
                newCategory.setStatus(pendingStatus);
                categoryRepository.saveAndFlush(newCategory);

                product.setCategory(newCategory);
                product.setVisible(false);
                product.setAvailable(false);

                // TODO: NOTIFY admin and pup about that
            }

            // Processing timeManagement/Duration/Booking rules
            //    timeManagement = true => "Manual" flexible time management
            //    timeManagement = false => "Fixed" time, fixed duration of event
            // - if manualTimeSelection == true => timeManagement = true
            // - if noTimeSelectionRequired == true => can be assumed as timeManagement = false, serviceDuration = 0/null
            //      noTimeSelectionRequired is a flag for services/products like purchases.

            if (Boolean.TRUE.equals(createProductRequestDTO.getNoTimeSelectionRequired())) {
                // Auto => timeManagement = false
                product.setTimeManagement(false);
                product.setServiceDurationMinMinutes(null);
                product.setServiceDurationMaxMinutes(null);
            } else if (Boolean.TRUE.equals(createProductRequestDTO.getManualTimeSelection())) {
                // Manual => timeManagement = true
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

            if (createProductRequestDTO.getSuitableEventTypes() != null
            && !createProductRequestDTO.getSuitableEventTypes().isEmpty()) {
                 for (Long eventTypeId : createProductRequestDTO.getSuitableEventTypes()) {
                     EventType eventType = eventTypesRepository.findById(eventTypeId)
                             .orElseThrow(() -> new EventNotFoundException("Event type not found: " + eventTypeId));
                     EventTypeProductLink link = new EventTypeProductLink();
                     link.setEventType(eventType);
                     link.setProduct(product);

                     product.getSuitableEventTypeLinks().add(link);
                     eventType.getProductLinks().add(link);
                 }
            }

            product.setLikes(0L);

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

       public List<ProductDto> findAll(PageRequest of) { return productRepository.findAll(of).stream().map(ProductDto::fromProduct).toList();}

       public List<ProductDto> findTopFive() { return productRepository.findAll().stream().map(ProductDto::fromProduct).toList();}

}
