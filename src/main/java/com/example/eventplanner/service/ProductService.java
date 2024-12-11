package com.example.eventplanner.service;

import com.example.eventplanner.dto.eventDto.EventDto;
import com.example.eventplanner.dto.product.ProductDto;
import com.example.eventplanner.model.product.Product;
import com.example.eventplanner.repository.ProductRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

// Категорию продукта (из выбранных категорий PUP-а, на которые он зарегистрирован).
// Если подходящей категории для продукта нет,
// PUP может предложить новую категорию при создании продукта.
// В этом случае при нажатии на кнопку «Отправить» продукт не создается сразу,
// а переводится в статус «Ожидание», и администратору отправляется уведомление о предложенной категории.
// Название, описание, цену, скидку и изображения.
// Типы событий, к которым продукт привязан (0 или более).
// Видимость (доступность продукта для OD) и доступность продукта для покупки конечными пользователями


@Service
public class ProductService {
       private final ProductRepository productRepository;
       private final PhotoService photoService;

       public ProductService(ProductRepository productRepository,
                             PhotoService photoService) {
           this.productRepository = productRepository;
           this.photoService = photoService;
       }

       public Product registration(ProductDto productDto) {
           Product product = new Product();
           product.setProductCategory(productDto.getProductCategory());
           product.setProductName(productDto.getProductName());
           product.setProductDescription(productDto.getProductDescription());
           product.setProductPrice(productDto.getProductPrice());
           product.setProductDiscount(productDto.getProductDiscount());
           product.setPhoto(photoService.createPhoto(productDto.getPhotoDto()));
           product.setProductTypeOfEventsWhereItsApplicable(productDto.getProductTypeOfEventsWhereItsApplicable());
//           product.setProductIsActiveForOD(productDto.getProductIsActiveForOD());
//           product.setProductIsActiveForUsers(productDto.getProductIsActiveForUsers());
           productRepository.saveAndFlush(product);
           return product;
       }

       public void update (ProductDto productDto) {
           Product product = new Product();
           product.setProductCategory(productDto.getProductCategory());
           product.setProductName(productDto.getProductName());
           product.setProductDescription(productDto.getProductDescription());
           product.setProductPrice(productDto.getProductPrice());
           product.setProductDiscount(productDto.getProductDiscount());
//           product.setPhoto(photoService.createPhoto(productDto.getPhotoDto()));
           product.setProductTypeOfEventsWhereItsApplicable(productDto.getProductTypeOfEventsWhereItsApplicable());
//           product.setProductIsActiveForOD(productDto.getProductIsActiveForOD());
//           product.setProductIsActiveForUsers(productDto.getProductIsActiveForUsers());
           product.setProductRegistrationDate(new Date());
           productRepository.saveAndFlush(product);
       }

       public void productIsActiveForOD(Long productId) {
           productRepository.findById(productId).ifPresent(product -> product.setProductIsActiveForOD(true));
       }

       public void productIsActiveForUsers(Long productId) {
           productRepository.findById(productId).ifPresent(product -> product.setProductIsActiveForUsers(false));
       }

    public List<ProductDto> findAll(PageRequest of) {
           return productRepository.findAll(of).stream().map(ProductDto::fromProduct).toList();
    }

    public List<ProductDto> findTop5() {
        return productRepository.findAll().stream().map(ProductDto::fromProduct).toList();
}
}
