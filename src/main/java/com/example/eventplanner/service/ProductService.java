package com.example.eventplanner.service;

import com.example.eventplanner.dto.eventDto.EventDto;
import com.example.eventplanner.dto.product.CreateProductRequestDTO;
import com.example.eventplanner.dto.product.ProductDto;
import com.example.eventplanner.model.product.Product;
import com.example.eventplanner.repository.CategoryRepository;
import com.example.eventplanner.repository.ProductPhotosRepository;
import com.example.eventplanner.repository.ProductRepository;
import com.example.eventplanner.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
@AllArgsConstructor
public class ProductService {

       private final ProductRepository productRepository;
       private final CategoryRepository categoryRepository;
       private final UserRepository userRepository;
       private final ProductPhotosRepository productPhotosRepository;

       public Product create(CreateProductRequestDTO createProductRequestDTO, String pupEmail) {

           Product product = new Product();
           product.setPup(userRepository.findByEmail(pupEmail).orElseThrow());

           product.setName(createProductRequestDTO.getName());
           product.setPrice(createProductRequestDTO.getPrice());
           product.setDescription(createProductRequestDTO.getDescription());


       }

//       public void update (ProductDto productDto) {
//           Product product = new Product();
//           productRepository.saveAndFlush(product);
//       }

       public List<ProductDto> findAll(PageRequest of) { return productRepository.findAll(of).stream().map(ProductDto::fromProduct).toList();}

       public List<ProductDto> findTopFive() { return productRepository.findAll().stream().map(ProductDto::fromProduct).toList();}

}
