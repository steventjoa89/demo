package com.example.demo.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OrderDetailsDTO {
    private Integer orderId;
    private UserDTO user;
    private List<ItemDTO> items;
    private double totalAmount;
}
