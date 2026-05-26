package com.example.orderservice.seller.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

interface SellerJpaRepository extends JpaRepository<SellerJpaEntity, Long> {
}
