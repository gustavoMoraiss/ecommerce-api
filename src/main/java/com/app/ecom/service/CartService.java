package com.app.ecom.service;


import com.app.ecom.dto.CartItemRequest;
import com.app.ecom.model.CartItem;
import com.app.ecom.model.Product;
import com.app.ecom.model.User;
import com.app.ecom.repository.CartItemRepository;
import com.app.ecom.repository.ProductRepository;
import com.app.ecom.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public boolean addToCart(String userId, CartItemRequest cartItemRequest) {
        Optional<Product> productOpt = productRepository.findById(cartItemRequest.getProductId());

        if (productOpt.isEmpty()) return false;
        if (productOpt.get().getStockQuantity() < cartItemRequest.getQuantity()) return false;

        Optional<User> userOpt = userRepository.findById(Long.valueOf(userId));

        if (userOpt.isEmpty()) return false;

        Optional<CartItem> existingCartItemOpt = cartItemRepository.findByUserAndProduct(userOpt.get(), productOpt.get());
        if (existingCartItemOpt.isPresent()) {
            CartItem cartItem = existingCartItemOpt.get();
            cartItem.setQuantity(cartItem.getQuantity() + cartItemRequest.getQuantity());
            cartItem.setPrice(productOpt.get().getPrice().multiply(BigDecimal.valueOf(cartItemRequest.getQuantity())));
            cartItemRepository.save(cartItem);
            return true;
        }
        CartItem cartItem = new CartItem();
        cartItem.setUser(userOpt.get());
        cartItem.setProduct(productOpt.get());
        cartItem.setQuantity(cartItemRequest.getQuantity());
        cartItem.setPrice(productOpt.get().getPrice().multiply(BigDecimal.valueOf(cartItemRequest.getQuantity())));
        cartItemRepository.save(cartItem);
        return true;
    }

    public boolean deleteItemFromCart(String userId, Long productId) {
        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isEmpty()) return false;

        Optional<User> userOpt = userRepository.findById(Long.valueOf(userId));
        if (userOpt.isEmpty()) return false;

        Optional<CartItem> carItemOpt = cartItemRepository.findByUserAndProduct(userOpt.get(), productOpt.get());
        if (carItemOpt.isEmpty()) return false;

        cartItemRepository.delete(carItemOpt.get());

        return true;
    }
}
