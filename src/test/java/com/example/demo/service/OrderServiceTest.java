package com.example.demo.service;

import com.example.demo.dto.OrderSummaryDTO;
import com.example.demo.dto.PaginatedResponse;
import com.example.demo.entity.Order;
import com.example.demo.entity.OrderItem;
import com.example.demo.entity.Product;
import com.example.demo.entity.User;
import com.example.demo.exception.DataNotFoundException;
import com.example.demo.repository.OrderItemRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.request.OrderItemRequest;
import com.example.demo.request.OrderRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    @Mock
    private JwtService jwtService;
    @Mock
    private UserService userService;
    @Mock
    private ProductService productService;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderItemRepository orderItemRepository;

    @InjectMocks
    private OrderService orderService;

    private User user;
    private Order order;
    private Product product;
    private OrderItem orderItem;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(1);
        user.setName("Steven");

        product = new Product();
        product.setId(1);
        product.setName("Sample Product");
        product.setPrice(100.0);

        order = new Order();
        order.setId(1);
        order.setUser(user);

        orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setQuantity(2);
        order.setOrderItems(Collections.singletonList(orderItem));
    }

    @Test
    void createOrder_Success() {
        when(jwtService.getUsernameFromToken("token")).thenReturn("Steven");
        when(userService.checkIfNameExists("Steven")).thenReturn(user);
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setItems(Collections.singletonList(new OrderItemRequest(1, 3)));

        Integer orderId = orderService.createOrder("token", orderRequest);

        assertNotNull(orderId);
        assertEquals(1, orderId);
    }

    @Test
    void updateOrderById_Success() {
        when(jwtService.getUsernameFromToken("token")).thenReturn("Steven");
        when(userService.checkIfNameExists("Steven")).thenReturn(user);
        when(orderRepository.findById(1)).thenReturn(Optional.of(order));
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setItems(Collections.singletonList(new OrderItemRequest(1, 5)));

        assertDoesNotThrow(() -> orderService.updateOrderById("token", 1, orderRequest));
        verify(orderItemRepository).deleteByOrder(order);
    }

    @Test
    void addProductToOrder_NewProduct() {
        when(orderRepository.findById(1)).thenReturn(Optional.of(order));
        when(productService.getProductById(2)).thenReturn(product);

        OrderItemRequest newItem = new OrderItemRequest(2, 5);

        assertDoesNotThrow(() -> orderService.addProductToOrder(1, newItem));
        verify(orderRepository).save(order);
    }

    @Test
    void getOrderById_Success() {
        when(orderRepository.findById(1)).thenReturn(Optional.of(order));

        Order result = orderService.getOrderById(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
    }

    @Test
    void getOrderById_NotFound() {
        when(orderRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> orderService.getOrderById(1));
    }

    @Test
    void deleteOrderById_Success() {
        when(orderRepository.findById(1)).thenReturn(Optional.of(order));

        assertDoesNotThrow(() -> orderService.deleteOrderById(1));
        verify(orderRepository).deleteById(1);
    }

    @Test
    void getAllOrders_Success() {
        Page<Order> page = new PageImpl<>(Collections.singletonList(order));
        when(orderRepository.findAll(any(Pageable.class))).thenReturn(page);

        PaginatedResponse<OrderSummaryDTO> response = orderService.getAllOrders(1, 5, "id");

        assertNotNull(response);
        assertEquals(1, response.getTotalPages());
    }
}