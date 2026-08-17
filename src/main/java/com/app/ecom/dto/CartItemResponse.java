package com.app.ecom.dto;

import com.app.ecom.model.Product;
import com.app.ecom.model.User;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartItemResponse {
    private Long id;
    private User user;
    private Product product;
    private Integer quantity;
    private BigDecimal price;
}
