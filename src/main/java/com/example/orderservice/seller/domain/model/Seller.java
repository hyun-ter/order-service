package com.example.orderservice.seller.domain.model;

import java.time.LocalDateTime;

/**
 * 판매자 Aggregate Root.
 *
 * <p>도메인 레이어에 위치하며 Spring/JPA 등 외부 의존성을 갖지 않는 순수 Java 객체다.
 * 상품 등록 시 {@link SellerId}를 Product에 ID 참조로 전달한다.
 */
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

    /**
     * 새 판매자를 등록할 때 사용하는 팩토리 메서드.
     * 초기 상태는 {@code ACTIVE}이며, sellerId는 JPA 저장 후 부여된다.
     */
    public static Seller register(BusinessName businessName, Contact contact) {
        return new Seller(null, businessName, contact, SellerStatus.ACTIVE, LocalDateTime.now());
    }

    /**
     * DB에서 조회한 데이터로 판매자 객체를 재구성하는 팩토리 메서드.
     * Infrastructure 레이어(SellerRepositoryImpl)에서만 호출한다.
     */
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
