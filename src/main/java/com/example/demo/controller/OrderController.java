package com.example.demo.controller;

import com.example.demo.dto.OrderDetailsDTO;
import com.example.demo.dto.OrderSummaryDTO;
import com.example.demo.dto.PaginatedResponse;
import com.example.demo.entity.Order;
import com.example.demo.request.OrderItemRequest;
import com.example.demo.request.OrderRequest;
import com.example.demo.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    @Autowired
    OrderService orderService;

    @PostMapping
    public ResponseEntity<Map<String, Integer>> createOrder(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @RequestBody OrderRequest request) {
        Integer orderId = orderService.createOrder(token, request);

        Map<String, Integer> response = Map.of("id", orderId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> updateOrderById(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @PathVariable Integer id, @RequestBody OrderRequest request) {
        orderService.updateOrderById(token, id, request);
        return ResponseEntity.ok(Map.of("message", "Order updated successfully"));
    }

    @PatchMapping("/{id}/add")
    public ResponseEntity<Map<String, String>> addProductToOrder(@PathVariable Integer id, @RequestBody OrderItemRequest newItem) {
        orderService.addProductToOrder(id, newItem);
        return ResponseEntity.ok(Map.of("message", "Product added successfully"));
    }

    @DeleteMapping("/{orderId}/remove/{productId}")
    public ResponseEntity<Map<String, String>> removeProductFromOrder(@PathVariable Integer orderId, @PathVariable Integer productId) {
        orderService.removeProductFromOrder(orderId, productId);
        return ResponseEntity.ok(Map.of("message", "Product removed successfully"));
    }

    @GetMapping
    public ResponseEntity<PaginatedResponse<OrderSummaryDTO>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {

        PaginatedResponse<OrderSummaryDTO> orders = orderService.getAllOrders(page, size, sortBy);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    public OrderDetailsDTO getOrderDetailsById(@PathVariable Integer id) {
        return orderService.getOrderDetailsById(id);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteOrderById(@PathVariable Integer id) {
        orderService.deleteOrderById(id);
        return ResponseEntity.noContent().build();
    }

}
