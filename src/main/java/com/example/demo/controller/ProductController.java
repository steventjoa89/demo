package com.example.demo.controller;

import com.example.demo.dto.PaginatedResponse;
import com.example.demo.entity.Product;
import com.example.demo.request.ProductRequest;
import com.example.demo.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    @Autowired
    ProductService productService;

    @PostMapping
    public ResponseEntity<Map<String, Integer>> createProduct(@Valid @RequestBody ProductRequest request) {
        Integer productId = productService.addProduct(request);
        Map<String, Integer> response = Map.of("id", productId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> updateProduct(@PathVariable Integer id, @Valid @RequestBody ProductRequest request) {
        productService.editProduct(id, request);
        return ResponseEntity.ok(Map.of("message", "Product updated successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Integer id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @GetMapping
    public PaginatedResponse<Product> getAllProducts(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "name") String sortBy) {
        return productService.getProducts(page, size, sortBy);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Integer id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
