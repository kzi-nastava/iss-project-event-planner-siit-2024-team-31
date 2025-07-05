package com.example.eventplanner.service;

import com.example.eventplanner.dto.TempPhotoUrlAndIdDTO;
import com.example.eventplanner.dto.eventDto.eventType.EventTypeDTO;
import com.example.eventplanner.dto.product.CreateProductRequestDTO;
import com.example.eventplanner.dto.product.ProductDTO;
import com.example.eventplanner.dto.product.UpdateProductRequestDTO;
import com.example.eventplanner.dto.product_category.ProductCategoryDTO;
import com.example.eventplanner.exception.exceptions.eventType.EventTypeNotFoundException;
import com.example.eventplanner.exception.exceptions.user.UserNotFoundException;
import com.example.eventplanner.model.EntityBase;
import com.example.eventplanner.model.ItemPhoto;
import com.example.eventplanner.model.Status;
import com.example.eventplanner.model.product.Product;
import com.example.eventplanner.model.product.ProductCategory;
import com.example.eventplanner.repository.*;
import com.example.eventplanner.utils.types.ProductFilterCriteria;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final UserRepository userRepository;
    private final StatusRepository statusRepository;
    private final PhotoService photoService;
    private final EventTypesRepository eventTypesRepository;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    public List<ProductDTO> getTop5Products() {
        return productRepository
                .findTop5ByOrderByRatingDesc()
                .stream()
                .map(this::productToProductDTO)
                .collect(Collectors.toList());
    }

    public Page<ProductDTO> searchProducts(String keyword, Pageable pageable) {
        if (keyword == null || keyword.isEmpty()) {
            return productRepository.findAll(pageable)
                    .map(this::productToProductDTO);
        } else {
            return productRepository.findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                    keyword, keyword, pageable
            ).map(this::productToProductDTO);
        }
    }

    public void update(Long productId, UpdateProductRequestDTO dto, String pupEmail) {
        var pup = userRepository.findByEmail(pupEmail)
                .orElseThrow(() -> new UserNotFoundException("PUP not found with email: " + pupEmail));

        var product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + productId));

        if (!product.getPup().getId().equals(pup.getId())) {
            throw new IllegalArgumentException("Product does not belong to the PUP with email: " + pupEmail);
        }

        //TODO STUDENT 2: For add check if product is reserved
        //and if so, throw exception or store previous version of product
        //until reservation is completed or cancelled

        // Update product details
        product.setVersion(product.getVersion() + 1);
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPeculiarities(dto.getPeculiarities());
        product.setPrice(dto.getPrice());
        product.setDiscount(dto.getDiscount());
        product.setAvailable(dto.getIsAvailable());
        product.setVisible(dto.getIsVisible());

        //Photos
        List<MultipartFile> photos = dto.getPhotos();
        if (photos != null && !photos.isEmpty()) {
            photoService.uploadPhotos(photos, bucketName, "product-photos");
        }

        List<Long> deletedPhotosIds = dto.getDeletedPhotosIds();
        if (deletedPhotosIds != null && !deletedPhotosIds.isEmpty()) {
            for (Long photoId : deletedPhotosIds) {
                ItemPhoto photo = product.getPhotos().stream()
                        .filter(p -> p.getId().equals(photoId))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("Photo not found with id: " + photoId));
                photoService.deletePhotoByKey(photo.getPhotoUrl(), bucketName);
                product.getPhotos().remove(photo);
            }
        }

        //Suitable event types
        if (dto.getSuitableEventTypes() != null && !dto.getSuitableEventTypes().isEmpty()) {
            product.setSuitableEventTypes(
                    dto.getSuitableEventTypes().stream()
                            .map(id -> eventTypesRepository.findById(id)
                                    .orElseThrow(() -> new EventTypeNotFoundException("Event type not found with id: " + id)))
                            .collect(Collectors.toList())
            );
        }

        productRepository.save(product);
    }

    public ProductDTO getProductDataForProviderById(Long productId, String pupEmail) {
        var pup = userRepository.findByEmail(pupEmail)
                .orElseThrow(() -> new UserNotFoundException("PUP not found with email: " + pupEmail));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + productId));

        if (!product.getPup().getId().equals(pup.getId())) {
            throw new IllegalArgumentException("Product does not belong to the PUP with email: " + pupEmail);
        }

        return productToProductDTO(product);
    }

    public Page<ProductDTO> filterSearchProducts(
            ProductFilterCriteria c,
            Pageable pageable) {

        Specification<Product> spec = Specification.where(null);

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

        if (c.getSuitableFor() != null && !c.getSuitableFor().isEmpty()) {
            spec = spec.and((root, query, cb) -> {
                query.distinct(true);
                var join = root.join("suitableEventTypes");
                return join.get("id").in(c.getSuitableFor());
            });
        }

        return productRepository
                .findAll(spec, pageable)
                .map(this::productToProductDTO);
    }

    public ProductFilterCriteria getFilterOptions() {
        List<Long> categoryIds = productRepository
                .findAll()
                .stream()
                .map(product -> product.getCategory().getId())
                .distinct()
                .collect(Collectors.toList());

        Double minPrice = productRepository.findAll().stream()
                .map(Product::getPrice)
                .min(Double::compareTo)
                .orElse(0.0);

        Double maxPrice = productRepository.findAll().stream()
                .map(Product::getPrice)
                .max(Comparator.naturalOrder())
                .orElse(0.0);

        return ProductFilterCriteria.builder()
                .categoryIds(categoryIds)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .minRating(0.0)
                .isAvailable(null)
                .build();
    }

    public Page<ProductDTO> searchMyProducts(String pupEmail, Pageable pageable) {
        var pup = userRepository.findByEmail(pupEmail)
                .orElseThrow(() -> new IllegalArgumentException("PUP not found with email: " + pupEmail));

        return productRepository.findAllByPup(pup, pageable)
                .map(this::productToProductDTO);
    }

    public void create(CreateProductRequestDTO productDto, String pupEmail) {
        var pup = userRepository.findByEmail(pupEmail)
                .orElseThrow(() -> new UserNotFoundException("PUP not found with email: " + pupEmail));

        Product newProduct = new Product();

        //Category
        var category = productDto.getCategory();
        if (category == null || category.isBlank()) {
            throw new IllegalArgumentException("Product category cannot be null or empty");
        }

        Optional<ProductCategory> productCategory = productCategoryRepository.findByName(category);

        if (productCategory.isEmpty()) {
            ProductCategory newProductCategory = new ProductCategory();
            newProductCategory.setName(category);
            newProductCategory.setDescription("Default description for " + category);
            Status pendingStatus = statusRepository.findByName("PENDING");
            newProductCategory.setStatus(pendingStatus);
            newProduct.setCategory(newProductCategory);
            newProduct.setStatus(pendingStatus);
        }
        else {
           newProduct.setCategory(productCategory.get());
           newProduct.setStatus(statusRepository.findByName("ACTIVE"));
        }

        newProduct.setPup(pup);
        newProduct.setName(productDto.getName());
        newProduct.setDescription(productDto.getDescription());
        newProduct.setPeculiarities(productDto.getPeculiarities());
        newProduct.setPrice(productDto.getPrice());
        newProduct.setDiscount(productDto.getDiscount());
        newProduct.setVisible(productDto.getIsVisible());
        newProduct.setAvailable(productDto.getIsAvailable());

        // Set photos
        List<MultipartFile> photos = productDto.getPhotos();
        if (photos != null && !photos.isEmpty()) {
            String photosPrefix = "product-photos";
            List<String> photoUrls = photoService.uploadPhotos(photos, bucketName, photosPrefix);

            for (String url : photoUrls) {
                ItemPhoto itemPhoto = new ItemPhoto();
                itemPhoto.setPhotoUrl(url);
                itemPhoto.setProduct(newProduct);
                newProduct.getPhotos().add(itemPhoto);
            }
        }

        // Set suitable event types
        newProduct.setSuitableEventTypes(
                productDto.getSuitableEventTypes().stream()
                        .map(id -> eventTypesRepository.findById(id)
                                .orElseThrow(() -> new EventTypeNotFoundException("Event type not found with id: " + id)))
                        .collect(Collectors.toList())
        );

        productRepository.save(newProduct);
    }

    // Helper methods for creating products, getting user email, etc. can be added here
    public ProductDTO productToProductDTO(com.example.eventplanner.model.product.Product product) {
        ProductDTO productDTO = new ProductDTO();

        ProductCategoryDTO categoryDTO = new ProductCategoryDTO();
        categoryDTO.setId(product.getCategory().getId());
        categoryDTO.setName(product.getCategory().getName());
        categoryDTO.setDescription(product.getCategory().getDescription());
        categoryDTO.setStatus(product.getCategory().getStatus());

        productDTO.setCategory(categoryDTO);
        productDTO.setId(product.getId());
        productDTO.setPupId(product.getPup().getId());
        productDTO.setName(product.getName());
        productDTO.setDescription(product.getDescription());
        productDTO.setPeculiarities(product.getPeculiarities());
        productDTO.setPrice(product.getPrice());
        productDTO.setDiscount(product.getDiscount());
        productDTO.setSuitableEventTypes(
                product.getSuitableEventTypes().stream()
                        .map(eventType -> {
                            EventTypeDTO eventTypeDTO = new EventTypeDTO();
                            eventTypeDTO.setId(eventType.getId());
                            eventTypeDTO.setName(eventType.getName());
                            eventTypeDTO.setDescription(eventType.getDescription());
                            return eventTypeDTO;
                        })
                        .collect(Collectors.toList())
        );
        productDTO.setVisible(product.isVisible());
        productDTO.setAvailable(product.isAvailable());
        productDTO.setRating(product.getRating());
        productDTO.setStatus(product.getStatus());

        List<ItemPhoto> photoUrls = product.getPhotos();
        if (photoUrls != null && !photoUrls.isEmpty()) {
            List<TempPhotoUrlAndIdDTO> tempPhotoUrlAndIdDTOList = new ArrayList<>();
            for (ItemPhoto photo : photoUrls) {
                TempPhotoUrlAndIdDTO tempPhotoUrlAndIdDTO = new TempPhotoUrlAndIdDTO();
                tempPhotoUrlAndIdDTO.setTempPhotoUrl(photoService.generatePresignedUrl(photo.getPhotoUrl(), bucketName));
                tempPhotoUrlAndIdDTO.setPhotoId(photo.getId());
                tempPhotoUrlAndIdDTOList.add(tempPhotoUrlAndIdDTO);
            }
            productDTO.setPhotos(tempPhotoUrlAndIdDTOList);
        }

        return productDTO;
    }



}
