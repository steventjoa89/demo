package com.example.demo.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderRequest {
    private String name;
    private List<OrderItemRequest> items;
}
