//package com.example.demo.service.impl;
//
//import com.example.demo.entity.Product;
//import com.example.demo.entity.User;
//import com.example.demo.repository.ProductRepository;
//import com.example.demo.repository.UserRepository;
//import com.example.demo.request.ProductRequest;
//import com.example.demo.service.ProductService;
//import com.example.demo.service.UserService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Service;
//import org.springframework.web.server.ResponseStatusException;
//
//import java.util.List;
//
//@Service
//public class ProductServiceImpl implements ProductService {
//    @Autowired
//    private ProductRepository productRepository;
//
//
//    @Override
//    public Product addProduct(ProductRequest request) {
//        Product product = new Product();
//        product.setName(request.getName());
//        product.setType(request.getType());
//        product.setPrice(request.getPrice());
//        return productRepository.save(product);
//    }
//
//    @Override
//    public Product editProduct(Integer id, ProductRequest request) {
//        return productRepository.findById(id).map(product -> {
//            product.setName(request.getName());
//            product.setType(request.getType());
//            product.setPrice(request.getPrice());
//            return productRepository.save(product);
//        }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
//    }
//
//    @Override
//    public Product getProductById(Integer id) {
//        return productRepository.findById(id)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
//    }
//
//    @Override
//    public List<Product> getProducts(int page, int size) {
//        Pageable pageable = PageRequest.of(page, size);
//        return productRepository.findAll(pageable).getContent();
//    }
//
//    @Override
//    public boolean deleteProduct(Integer id) {
//        if (productRepository.existsById(id)) {
//            productRepository.deleteById(id);
//            return true;
//        }else{
//            return false;
//        }
//    }
//}
