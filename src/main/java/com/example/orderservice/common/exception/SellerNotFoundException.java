package com.example.orderservice.common.exception;

public class SellerNotFoundException extends RuntimeException {

    public SellerNotFoundException(Long sellerId) {
        super("Seller not found: " + sellerId);
    }
}
