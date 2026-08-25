package com.app.ecom.service;


import com.app.ecom.dto.CartItemRequest;
import com.app.ecom.dto.CartItemResponse;
import com.app.ecom.model.CartItem;
import com.app.ecom.model.Product;
import com.app.ecom.model.User;
import com.app.ecom.repository.CartItemRepository;
import com.app.ecom.repository.ProductRepository;
import com.app.ecom.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public @Nullable List<CartItemResponse> fetchAllCartItem(String userId) {
        Optional<List<CartItem>> cartItems = userRepository.findById(Long.valueOf(userId))
                .map(cartItemRepository::findByUser);

        return cartItems.map(items -> items.stream()
                .map(this::mapToCartItemResponse).collect(Collectors.toList())).orElse(List.of());

    }

    private CartItemResponse mapToCartItemResponse(CartItem cartItem) {
        CartItemResponse cartItemResponse = new CartItemResponse();
        cartItemResponse.setId(cartItem.getId());
        cartItemResponse.setProduct(cartItem.getProduct());
        cartItemResponse.setUser(cartItem.getUser());
        cartItemResponse.setPrice(cartItem.getPrice());
        cartItemResponse.setQuantity(cartItem.getQuantity());
        return cartItemResponse;
    }

    public void clearCart(String userId) {
        Optional<User> userOpt = userRepository.findById(Long.valueOf(userId));
        userOpt.ifPresent(cartItemRepository::deleteByUser);
    }
}
