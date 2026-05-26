package com.example.orderservice.order.domain.model;

import com.example.orderservice.common.vo.Money;
import com.example.orderservice.product.domain.model.ProductId;

/**
 * 주문 항목 엔티티 (Order Aggregate 내부).
 *
 * <p>Order Aggregate Root에 속하는 엔티티로, 독립적으로 존재할 수 없다.
 * 주문 시점의 상품명과 단가를 스냅샷으로 저장한다.
 * 이후 상품 정보가 변경되더라도 주문 내역은 원래 값을 유지한다.
 */
public class OrderItem {

    private final ProductId productId;
    private final String productName;  // 주문 시점의 상품명 스냅샷
    private final Money unitPrice;     // 주문 시점의 단가 스냅샷
    private final int quantity;

    private OrderItem(ProductId productId, String productName, Money unitPrice, int quantity) {
        if (productId == null) throw new IllegalArgumentException("ProductId cannot be null");
        if (productName == null || productName.isBlank()) throw new IllegalArgumentException("Product name cannot be blank");
        if (unitPrice == null) throw new IllegalArgumentException("Unit price cannot be null");
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be positive");
        this.productId = productId;
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }

    /** 주문 항목을 생성하는 팩토리 메서드. */
    public static OrderItem of(ProductId productId, String productName, Money unitPrice, int quantity) {
        return new OrderItem(productId, productName, unitPrice, quantity);
    }

    /** 이 항목의 소계(단가 × 수량)를 반환한다. */
    public Money subtotal() {
        return unitPrice.multiply(quantity);
    }

    public ProductId getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public Money getUnitPrice() {
        return unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }
}
