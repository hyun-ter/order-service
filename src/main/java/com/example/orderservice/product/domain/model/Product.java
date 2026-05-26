package com.example.orderservice.product.domain.model;

import com.example.orderservice.common.vo.Money;
import com.example.orderservice.seller.domain.model.SellerId;

/**
 * 상품 Aggregate Root.
 *
 * <p>도메인 레이어에 위치하며 Spring/JPA 등 외부 의존성을 갖지 않는 순수 Java 객체다.
 * 판매자(Seller)를 직접 참조하지 않고 {@link SellerId}로 ID 참조한다 (도메인 간 경계 유지).
 *
 * <p>주문 생성 시 {@code OrderDomainService}가 상품의 상태가 {@code ON_SALE}인지 검증한다.
 */
public class Product {

    private final ProductId productId;
    private final SellerId sellerId;  // 판매자 도메인을 직접 참조하지 않고 ID만 보관
    private final String name;
    private final Money price;
    private final Category category;
    private final ProductStatus status;

    private Product(ProductId productId, SellerId sellerId, String name, Money price, Category category, ProductStatus status) {
        this.productId = productId;
        this.sellerId = sellerId;
        this.name = name;
        this.price = price;
        this.category = category;
        this.status = status;
    }

    /**
     * 새 상품을 등록할 때 사용하는 팩토리 메서드.
     * 초기 상태는 {@code ON_SALE}이며, productId는 JPA 저장 후 부여된다.
     */
    public static Product register(SellerId sellerId, String name, Money price, Category category) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Product name cannot be blank");
        return new Product(null, sellerId, name, price, category, ProductStatus.ON_SALE);
    }

    /**
     * DB에서 조회한 데이터로 상품 객체를 재구성하는 팩토리 메서드.
     * Infrastructure 레이어(ProductRepositoryImpl)에서만 호출한다.
     */
    public static Product restore(ProductId productId, SellerId sellerId, String name, Money price, Category category, ProductStatus status) {
        return new Product(productId, sellerId, name, price, category, status);
    }

    public ProductId getProductId() {
        return productId;
    }

    public SellerId getSellerId() {
        return sellerId;
    }

    public String getName() {
        return name;
    }

    public Money getPrice() {
        return price;
    }

    public Category getCategory() {
        return category;
    }

    public ProductStatus getStatus() {
        return status;
    }
}
