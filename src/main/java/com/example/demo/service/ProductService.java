package com.example.demo.service;

import com.example.demo.dto.PaginatedResponse;
import com.example.demo.entity.Product;
import com.example.demo.exception.DataNotFoundException;
import com.example.demo.repository.ProductRepository;
import com.example.demo.request.ProductRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.lang.reflect.Field;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    public Integer addProduct(ProductRequest request) {
        Product product = new Product();
        product.setName(request.getName());
        product.setType(request.getType());
        product.setPrice(request.getPrice());
        productRepository.save(product);

        return product.getId();
    }

    public void editProduct(Integer id, ProductRequest request) {
        productRepository.findById(id).map(product -> {
            product.setName(request.getName());
            product.setType(request.getType());
            product.setPrice(request.getPrice());
            return productRepository.save(product);
        }).orElseThrow(() -> new DataNotFoundException("Product not found"));
    }

    public Product getProductById(Integer id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Product not found"));
    }

    public PaginatedResponse<Product> getProducts(int page, int size, String sortBy) {
        if (!isValidField(Product.class, sortBy)) {
            sortBy = "name"; // Fallback to default
        }

        Pageable pageable = PageRequest.of(Math.max(page - 1, 0), size, Sort.by(sortBy));
        Page<Product> productPage = productRepository.findAll(pageable);

        return new PaginatedResponse<>(
                productPage.getContent(),
                productPage.getNumber() + 1,
                productPage.getSize(),
                productPage.getTotalElements(),
                productPage.getTotalPages()
        );
    }


    public void deleteProduct(Integer id) {
        productRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Product not found"));
        productRepository.deleteById(id);

    }

    private boolean isValidField(Class<?> clazz, String fieldName) {
        return Arrays.stream(clazz.getDeclaredFields())
                .map(Field::getName)
                .anyMatch(name -> name.equalsIgnoreCase(fieldName));
    }
}
