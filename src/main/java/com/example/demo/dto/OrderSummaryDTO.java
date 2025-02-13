package com.example.demo.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OrderSummaryDTO {
    private Integer orderId;
    private String user;
    private long totalOrder;
    private double totalPrice;
}
