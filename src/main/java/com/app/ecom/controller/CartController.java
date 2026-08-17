package com.app.ecom.controller;

import com.app.ecom.dto.CartItemRequest;
import com.app.ecom.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    @PostMapping
    public ResponseEntity<String> addToCart(
            @RequestHeader("X-User-ID") String userId,
            @RequestBody CartItemRequest cartItemRequest
    ) {
        boolean successResult = cartService.addToCart(userId, cartItemRequest);
        return successResult
                ? ResponseEntity.status(HttpStatus.CREATED).body("Item added to cart")
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to add item to cart");
    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<Void> removeFromCart(
            @RequestHeader("X-User-ID") String userId,
            @PathVariable Long productId
    ) {
        boolean successResult = cartService.deleteItemFromCart(userId, productId);
        return successResult
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

}
