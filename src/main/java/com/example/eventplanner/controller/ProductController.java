package com.example.eventplanner.controller;

import com.example.eventplanner.dto.product.CreateProductRequestDTO;
import com.example.eventplanner.dto.product.CreateProductResponseDTO;
import com.example.eventplanner.dto.product.ProductDto;
import com.example.eventplanner.service.JwtService;
import com.example.eventplanner.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
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
    private final JwtService jwtService;

    @PostMapping()
    public ResponseEntity<CreateProductResponseDTO> createProduct(@RequestBody CreateProductRequestDTO productDto, HttpServletRequest request) {

        CreateProductResponseDTO response = new CreateProductResponseDTO();

        try {

            String authorizationHeader = request.getHeader("Authorization");
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                throw new IllegalArgumentException("Invalid Authorization header");
            }

            String token = authorizationHeader.substring(7);

            //extracts user email
            String pupEmail = jwtService.extractUsername(token);

            //productService.create(productDto, pupEmail);
            response.setMessage("Product creation is not implemented, in progress... ");
            //response.setMessage("Product created successfully");
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
