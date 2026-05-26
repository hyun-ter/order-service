package com.example.orderservice.seller.infrastructure.persistence;

import com.example.orderservice.seller.domain.model.Seller;
import com.example.orderservice.seller.domain.model.SellerId;
import com.example.orderservice.seller.domain.repository.SellerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class SellerRepositoryImpl implements SellerRepository {

    private final SellerJpaRepository sellerJpaRepository;

    @Override
    public Seller save(Seller seller) {
        SellerJpaEntity entity = SellerJpaEntity.fromSeller(seller);
        SellerJpaEntity saved = sellerJpaRepository.save(entity);
        return saved.toSeller();
    }

    @Override
    public Optional<Seller> findById(SellerId sellerId) {
        return sellerJpaRepository.findById(sellerId.getId())
                .map(SellerJpaEntity::toSeller);
    }
}
