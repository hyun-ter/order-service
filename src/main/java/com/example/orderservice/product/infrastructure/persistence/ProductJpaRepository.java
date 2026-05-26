package com.example.orderservice.product.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

interface ProductJpaRepository extends JpaRepository<ProductJpaEntity, Long> {

    List<ProductJpaEntity> findBySellerId(Long sellerId);

    List<ProductJpaEntity> findByCategory(String category);
}
