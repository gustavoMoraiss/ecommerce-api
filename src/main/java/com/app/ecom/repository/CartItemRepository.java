package com.app.ecom.repository;

import com.app.ecom.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface CartItemRepository extends JpaRepository<CartItem, Long> {
}
