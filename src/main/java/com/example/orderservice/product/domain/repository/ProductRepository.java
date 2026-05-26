package com.example.orderservice.product.domain.repository;

import com.example.orderservice.product.domain.model.Category;
import com.example.orderservice.product.domain.model.Product;
import com.example.orderservice.product.domain.model.ProductId;
import com.example.orderservice.seller.domain.model.SellerId;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {

    Product save(Product product);

    Optional<Product> findById(ProductId productId);

    List<Product> findBySellerId(SellerId sellerId);

    List<Product> findByCategory(Category category);
}
