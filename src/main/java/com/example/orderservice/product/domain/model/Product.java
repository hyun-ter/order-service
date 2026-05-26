package com.example.orderservice.product.domain.model;

import com.example.orderservice.common.vo.Money;
import com.example.orderservice.seller.domain.model.SellerId;

public class Product {

    private final ProductId productId;
    private final SellerId sellerId;
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

    public static Product register(SellerId sellerId, String name, Money price, Category category) {
        return new Product(null, sellerId, name, price, category, ProductStatus.ON_SALE);
    }

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
