package com.example.demo.service;

import com.example.demo.dto.*;
import com.example.demo.entity.Order;
import com.example.demo.entity.OrderItem;
import com.example.demo.entity.Product;
import com.example.demo.entity.User;
import com.example.demo.exception.DataNotFoundException;
import com.example.demo.repository.OrderItemRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.request.OrderItemRequest;
import com.example.demo.request.OrderRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class OrderService {
    @Autowired
    JwtService jwtService;
    @Autowired
    UserService userService;
    @Autowired
    ProductService productService;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    OrderItemRepository orderItemRepository;

    @Transactional
    public Integer createOrder(String token, OrderRequest data) {
        User user = userService.checkIfNameExists(jwtService.getUsernameFromToken(token));
        Order order = new Order();
        order.setUser(user);
        order = orderRepository.save(order);

        saveOrderItem(order, data);

        orderRepository.save(order);
        return order.getId();
    }

    @Transactional
    public void updateOrderById(String token, Integer orderId, OrderRequest data) {
        User user = userService.checkIfNameExists(jwtService.getUsernameFromToken(token));

        Order order = getOrderById(orderId);
        order.setUser(user);

        orderItemRepository.deleteByOrder(order);
        saveOrderItem(order, data);

        orderRepository.save(order);
    }

    @Transactional
    public void addProductToOrder(Integer orderId, OrderItemRequest newItem) {
        Order order = getOrderById(orderId);
        Product product = productService.getProductById(newItem.getProductId());

        // Check if product already exists in the order
        Optional<OrderItem> existingItems = order.getOrderItems().stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .findFirst();

        if(existingItems.isPresent()){      // increase the qty
            OrderItem item = existingItems.get();
            item.setQuantity(item.getQuantity() + newItem.getQuantity());
        }else{                              // Push if not exists
            OrderItem newOrderItem = new OrderItem();
            newOrderItem.setOrder(order);
            newOrderItem.setProduct(product);
            newOrderItem.setQuantity(newItem.getQuantity());
            order.getOrderItems().add(newOrderItem);
        }

        orderRepository.save(order);
    }

    @Transactional
    public void removeProductFromOrder(Integer orderId, Integer productId) {
        Order order = getOrderById(orderId);
        boolean removed = order.getOrderItems().removeIf(item -> item.getProduct().getId().equals(productId));
        if (!removed) {
            throw new DataNotFoundException("Product not found in the order");
        }
        orderRepository.save(order);
    }

    private void saveOrderItem(Order order, OrderRequest data){
        Map<Integer, Integer> productQuantityMap = new HashMap<>();

        for (OrderItemRequest itemRequest : data.getItems()) {
            productQuantityMap.merge(itemRequest.getProductId(), itemRequest.getQuantity(), Integer::sum);
        }

        for (Map.Entry<Integer, Integer> entry : productQuantityMap.entrySet()) {
            Product product = productService.getProductById(entry.getKey());
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(entry.getValue());
            orderItemRepository.save(orderItem);
        }
    }

    public OrderDetailsDTO getOrderDetailsById(Integer id){
        Order order = getOrderById(id);

        List<ItemDTO> itemDTOs = order.getOrderItems().stream()
                .map(item -> new ItemDTO(
                        item.getProduct().getName(),
                        item.getQuantity(),
                        item.getProduct().getPrice(),
                        item.getQuantity() * item.getProduct().getPrice()
                ))
                .toList();

        double totalAmount = itemDTOs.stream().mapToDouble(ItemDTO::getTotal).sum();
        return new OrderDetailsDTO(order.getId(), new UserDTO(order.getUser().getId(), order.getUser().getName()), itemDTOs, totalAmount);
    }

    public Order getOrderById(Integer id){
        return orderRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Order not found"));
    }

    @Transactional
    public void deleteOrderById(Integer id) {
        orderRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Order not found"));
        orderRepository.deleteById(id);
    }

    public PaginatedResponse<OrderSummaryDTO> getAllOrders(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(Math.max(page - 1, 0), size, Sort.by(sortBy));
        Page<Order> orderPage = orderRepository.findAll(pageable);

        List<OrderSummaryDTO> orderSummaries = orderPage.getContent().stream()
                .map(order -> {
                    int totalOrder = order.getOrderItems().stream()
                            .mapToInt(OrderItem::getQuantity)
                            .sum();

                    double totalPrice = order.getOrderItems().stream()
                            .mapToDouble(item -> item.getQuantity() * item.getProduct().getPrice())
                            .sum();

                    return new OrderSummaryDTO(order.getId(), order.getUser().getName(), totalOrder, totalPrice);
                })
                .toList();

        return new PaginatedResponse<>(
                orderSummaries,
                orderPage.getNumber() + 1,
                orderPage.getSize(),
                orderPage.getTotalElements(),
                orderPage.getTotalPages()
        );
    }
}
