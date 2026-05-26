package com.example.orderservice.seller.domain.model;

import java.time.LocalDateTime;

public class Seller {

    private final SellerId sellerId;
    private final BusinessName businessName;
    private final Contact contact;
    private final SellerStatus status;
    private final LocalDateTime createdAt;

    private Seller(SellerId sellerId, BusinessName businessName, Contact contact, SellerStatus status, LocalDateTime createdAt) {
        this.sellerId = sellerId;
        this.businessName = businessName;
        this.contact = contact;
        this.status = status;
        this.createdAt = createdAt;
    }

    public static Seller register(BusinessName businessName, Contact contact) {
        return new Seller(null, businessName, contact, SellerStatus.ACTIVE, LocalDateTime.now());
    }

    public static Seller restore(SellerId sellerId, BusinessName businessName, Contact contact, SellerStatus status, LocalDateTime createdAt) {
        return new Seller(sellerId, businessName, contact, status, createdAt);
    }

    public SellerId getSellerId() {
        return sellerId;
    }

    public BusinessName getBusinessName() {
        return businessName;
    }

    public Contact getContact() {
        return contact;
    }

    public SellerStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
