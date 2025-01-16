package com.example.eventplanner.controller;



import com.example.eventplanner.dto.eventDto.EventDto;
import com.example.eventplanner.dto.product.CreateProductRequestDTO;
import com.example.eventplanner.dto.product.CreateProductResponseDTO;
import com.example.eventplanner.dto.product.ProductDto;
import com.example.eventplanner.model.product.Product;
import com.example.eventplanner.service.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/product")
@RestController
@AllArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping()
    public ResponseEntity<CreateProductResponseDTO> createProduct(@RequestBody CreateProductRequestDTO productDto) {

        CreateProductResponseDTO response = new CreateProductResponseDTO();
        try {
            response.setMessage("123");
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }
        catch (Exception e){
            response.setMessage("Error with product creation");
            response.setError("Error: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

//    @PutMapping
//    public ResponseEntity<?> createProduct(@RequestBody ProductDto productDto){
//            try{
//                Product product = productService.registration(productDto);
//                return ResponseEntity.ok(product);
//                }
//            catch (Exception e){
//                return ResponseEntity.badRequest().body(e.getMessage());
//            }
//    }

    // @get event погинация
    @GetMapping()
    public List<ProductDto> getEvents(@RequestParam(defaultValue = "0") int page) {
        return productService.findAll(PageRequest.of(page, 6)); // Возвращаем 6 элементов на странице
    }

    @GetMapping("/top")
    public ResponseEntity<List<ProductDto>> findTopFive() {
        return ResponseEntity.ok(productService.findTopFive());
    }




    //создание продукты, изменение, удаление и поиск с фильтром
    //пункт 4.2 добавить метод бронирования услуги
    // buy and favorite

}
