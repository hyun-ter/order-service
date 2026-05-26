package com.example.orderservice.order.domain.model;

import com.example.orderservice.common.vo.Money;
import com.example.orderservice.product.domain.model.ProductId;

public class OrderItem {

    private final ProductId productId;
    private final String productName;
    private final Money unitPrice;
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

    public static OrderItem of(ProductId productId, String productName, Money unitPrice, int quantity) {
        return new OrderItem(productId, productName, unitPrice, quantity);
    }

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
