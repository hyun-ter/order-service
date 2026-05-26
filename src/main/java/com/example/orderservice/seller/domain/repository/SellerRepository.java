package com.example.orderservice.seller.domain.repository;

import com.example.orderservice.seller.domain.model.Seller;
import com.example.orderservice.seller.domain.model.SellerId;

import java.util.Optional;

public interface SellerRepository {

    Optional<Seller> findById(SellerId sellerId);

    Seller save(Seller seller);
}
