package com.app.ecom.repository;

import com.app.ecom.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByIsActiveTrue();
    @Query("SELECT p\n" +
            "    FROM products p\n" +
            "    WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))\n" +
            "       OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Product> searchProducts(@Param("keyword") String keyword);

}
