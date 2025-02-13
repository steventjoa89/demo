//package com.example.demo.controller;
//
//import com.example.demo.entity.Product;
//import com.example.demo.request.ProductRequest;
//import com.example.demo.service.JwtService;
//import com.example.demo.service.ProductService;
//import jakarta.validation.Valid;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.server.ResponseStatusException;
//
//import java.util.List;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/product")
//public class ProductController {
//    @Autowired
//    ProductService productService;
//    @Autowired
//    JwtService jwtService;
//
//    private void validateToken(String token) {
//        try {
//            jwtService.validateToken(token);
//        } catch (Exception e) {
//            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
//        }
//    }
//    @PostMapping
//    public Product createProduct(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @Valid @RequestBody ProductRequest request) {
//        validateToken(token.replace("Bearer ", ""));
//
//        return productService.addProduct(request);
//    }
//
//    @PutMapping("/{id}")
//    public ResponseEntity<Product> updateProduct(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @PathVariable Integer id, @Valid @RequestBody ProductRequest request) {
//        validateToken(token.replace("Bearer ", ""));
//
//        Product updatedProduct = productService.editProduct(id, request);
//        return ResponseEntity.ok(updatedProduct);
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<Product> getProductById(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @PathVariable Integer id) {
//        validateToken(token.replace("Bearer ", ""));
//
//        return ResponseEntity.ok(productService.getProductById(id));
//    }
//
//    @GetMapping
//    public List<Product> getAllProducts(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
//        validateToken(token.replace("Bearer ", ""));
//        page = page-1;
//        return productService.getProducts(page, size);
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteProduct(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @PathVariable Integer id) {
//        validateToken(token.replace("Bearer ", ""));
//
//        boolean success = productService.deleteProduct(id);
//        return success ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
//    }
//}
