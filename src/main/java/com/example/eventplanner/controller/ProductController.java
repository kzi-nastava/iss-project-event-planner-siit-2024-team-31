package com.example.eventplanner.controller;



import com.example.eventplanner.dto.product.ProductDto;
import com.example.eventplanner.model.product.Product;
import com.example.eventplanner.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RequestMapping("/product")
@RestController

public class ProductController {

        private final ProductService productService;

        public ProductController(ProductService productService) {this.productService = productService;}

    @PutMapping
    public ResponseEntity<?> createProduct(@RequestBody ProductDto productDto){
            try{
                Product product = productService.registration(productDto);
                return ResponseEntity.ok(product);
                }
            catch (Exception e){
                return ResponseEntity.badRequest().body(e.getMessage());
            }
    }



    //создание продукты, изменение, удаление и поиск с фильтром
    //пункт 4.2 добавить метод бронирования услуги
    // buy and favorite

}
