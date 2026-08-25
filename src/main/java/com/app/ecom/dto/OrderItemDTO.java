package com.app.ecom.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemDTO {
    private Long id;
    private Long productId;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal subtotal;
}
