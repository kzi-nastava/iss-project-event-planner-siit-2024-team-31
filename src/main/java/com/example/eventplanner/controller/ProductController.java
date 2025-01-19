package com.example.eventplanner.controller;

import com.example.eventplanner.dto.product.ProductCategoryDTO;
import com.example.eventplanner.dto.product.CreateProductRequestDTO;
import com.example.eventplanner.dto.product.CreateProductResponseDTO;
import com.example.eventplanner.dto.product.ProductDto;
import com.example.eventplanner.exception.RoleAccessDeniedException;
import com.example.eventplanner.service.JwtService;
import com.example.eventplanner.service.ProductService;
import com.example.eventplanner.service.UserService;
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
    private final UserService userService;

    @PostMapping()
    public ResponseEntity<CreateProductResponseDTO> createProduct(@ModelAttribute CreateProductRequestDTO productDto, HttpServletRequest request) {

        CreateProductResponseDTO response = new CreateProductResponseDTO();

        try {

            String authorizationHeader = request.getHeader("Authorization");
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                throw new IllegalArgumentException("Invalid Authorization header");
            }

            String token = authorizationHeader.substring(7);

            //extracts user email
            String pupEmail = jwtService.extractUsername(token);

            //access management, only PUP can create Product/Service
            if (!userService.getPupAccess(pupEmail)) {
                throw new RoleAccessDeniedException();
            }

            productService.create(productDto, pupEmail);
            response.setMessage("Product created successfully");
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }
        catch (RoleAccessDeniedException e) {
            response.setMessage("ROLE ACCESS DENIED");
            response.setError(e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
        }
        catch (Exception e){
            response.setMessage("Error with product creation");
            response.setError("Error: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @GetMapping("/categories")
    public ResponseEntity<List<ProductCategoryDTO>> getAllCategories() {
        try {
            var categories = productService.getAllCategories();
            return new ResponseEntity<>(categories, HttpStatus.OK);
        }
        catch (Exception e) {
            System.out.print("Error occurred when retrieving all product categories.");
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
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
