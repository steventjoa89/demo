package com.example.demo.service;

import com.example.demo.dto.PaginatedResponse;
import com.example.demo.entity.Product;
import com.example.demo.exception.DataNotFoundException;
import com.example.demo.repository.ProductRepository;
import com.example.demo.request.ProductRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product product;
    private ProductRequest productRequest;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1);
        product.setName("Test Product");
        product.setType("Test Type");
        product.setPrice(100.0);

        productRequest = new ProductRequest();
        productRequest.setName("New Product");
        productRequest.setType("New Type");
        productRequest.setPrice(200.0);
    }

    @Test
    void addProduct_ShouldReturnProductId() {
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product savedProduct = invocation.getArgument(0);
            savedProduct.setId(1);
            return savedProduct;
        });

        Integer productId = productService.addProduct(productRequest);
        assertNotNull(productId);
        assertEquals(1, productId);
    }

    @Test
    void editProduct_ShouldUpdateProduct() {
        when(productRepository.findById(1)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        assertDoesNotThrow(() -> productService.editProduct(1, productRequest));

        verify(productRepository).save(any(Product.class));
    }

    @Test
    void editProduct_ShouldThrowExceptionWhenProductNotFound() {
        when(productRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> productService.editProduct(1, productRequest));
    }

    @Test
    void getProductById_ShouldReturnProduct() {
        when(productRepository.findById(1)).thenReturn(Optional.of(product));

        Product result = productService.getProductById(1);

        assertNotNull(result);
        assertEquals("Test Product", result.getName());
    }

    @Test
    void getProductById_ShouldThrowExceptionWhenNotFound() {
        when(productRepository.findById(1)).thenReturn(Optional.empty());
        assertThrows(DataNotFoundException.class, () -> productService.getProductById(1));
    }

    @Test
    void getProducts_ShouldReturnPaginatedResponse() {
        Page<Product> productPage = new PageImpl<>(Collections.singletonList(product));
        when(productRepository.findAll(any(PageRequest.class))).thenReturn(productPage);

        PaginatedResponse<Product> response = productService.getProducts(1, 10, "name");

        assertNotNull(response);
        assertEquals(1, response.getContent().size());
        assertEquals("Test Product", response.getContent().get(0).getName());
    }

    @Test
    void deleteProduct_ShouldDeleteProduct() {
        when(productRepository.findById(1)).thenReturn(Optional.of(product));
        doNothing().when(productRepository).deleteById(1);
        assertDoesNotThrow(() -> productService.deleteProduct(1));
        verify(productRepository).deleteById(1);
    }

    @Test
    void deleteProduct_ShouldThrowExceptionWhenNotFound() {
        when(productRepository.findById(1)).thenReturn(Optional.empty());
        assertThrows(DataNotFoundException.class, () -> productService.deleteProduct(1));
    }

    @Test
    void isValidField_ShouldFallbackToNameForInvalidField() {
        Page<Product> productPage = new PageImpl<>(Collections.singletonList(product));
        when(productRepository.findAll(any(PageRequest.class))).thenReturn(productPage);

        PaginatedResponse<Product> response = productService.getProducts(1, 10, "invalidField");

        assertNotNull(response);
        assertEquals(1, response.getContent().size());
        assertEquals("Test Product", response.getContent().get(0).getName());
    }

}