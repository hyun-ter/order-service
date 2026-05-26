package com.example.orderservice.product.infrastructure.persistence;

import com.example.orderservice.product.domain.model.Category;
import com.example.orderservice.product.domain.model.Product;
import com.example.orderservice.product.domain.model.ProductId;
import com.example.orderservice.product.domain.repository.ProductRepository;
import com.example.orderservice.seller.domain.model.SellerId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

    private final ProductJpaRepository productJpaRepository;

    @Override
    public Product save(Product product) {
        ProductJpaEntity entity = ProductJpaEntity.fromProduct(product);
        ProductJpaEntity saved = productJpaRepository.save(entity);
        return saved.toProduct();
    }

    @Override
    public Optional<Product> findById(ProductId productId) {
        return productJpaRepository.findById(productId.getId())
                .map(ProductJpaEntity::toProduct);
    }

    @Override
    public List<Product> findBySellerId(SellerId sellerId) {
        return productJpaRepository.findBySellerId(sellerId.getId()).stream()
                .map(ProductJpaEntity::toProduct)
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> findByCategory(Category category) {
        return productJpaRepository.findByCategory(category.getName()).stream()
                .map(ProductJpaEntity::toProduct)
                .collect(Collectors.toList());
    }
}
