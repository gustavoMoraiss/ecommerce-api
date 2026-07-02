package com.app.ecom.service;

import com.app.ecom.dto.ProductRequest;
import com.app.ecom.dto.ProductResponse;
import com.app.ecom.model.Product;
import com.app.ecom.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public ProductResponse createProduct(ProductRequest productRequest) {
        Product product = new Product();
        updateProductFromRequest(product, productRequest);
        Product savedProduct = productRepository.save(product);
        return mapToProductResponse(savedProduct);
    }

    public Optional<ProductResponse> updateProduct(Long id, ProductRequest productRequest) {
        return productRepository.findById(id).map(existingProduct -> {
            updateProductFromRequest(existingProduct, productRequest);
            Product updatedProduct = productRepository.save(existingProduct);
            return mapToProductResponse(updatedProduct);
        });
    }

    private ProductResponse mapToProductResponse(Product savedProduct) {
        ProductResponse productResponse = new ProductResponse();
        productResponse.setId(savedProduct.getId());
        productResponse.setName(savedProduct.getName());
        productResponse.setDescription(savedProduct.getDescription());
        productResponse.setCategory(savedProduct.getCategory());
        productResponse.setPrice(savedProduct.getPrice());
        productResponse.setImageUrl(savedProduct.getImageUrl());
        productResponse.setStockQuantity(savedProduct.getStockQuantity());
        productResponse.setIsActive(savedProduct.getIsActive());
        return productResponse;
    }

    private void updateProductFromRequest(Product product, ProductRequest productRequest) {
        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setCategory(productRequest.getCategory());
        product.setPrice(productRequest.getPrice());
        product.setImageUrl(productRequest.getImageUrl());
        product.setStockQuantity(productRequest.getStockQuantity());
    }

    public List<ProductResponse> fetchAllProducts() {
        return productRepository.findByIsActiveTrue().stream().map(this::mapToProductResponse).collect(Collectors.toList());
    }

    public Optional<ProductResponse> fetchProductById(Long id) {
        return productRepository.findById(id).map(this::mapToProductResponse);
    }

    public boolean deleteProduct(Long id) {
        return productRepository.findById(id).map(product -> {
            product.setIsActive(false);
            productRepository.save(product);
            return true;
        }).orElse(false);
    }

    public List<ProductResponse> searchProducts(String keyword) {
       return productRepository.searchProducts(keyword).stream().map(this::mapToProductResponse).collect(Collectors.toList());
    }
}
