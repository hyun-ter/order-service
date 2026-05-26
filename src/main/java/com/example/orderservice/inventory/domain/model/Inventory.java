package com.example.orderservice.inventory.domain.model;

import com.example.orderservice.product.domain.model.ProductId;

public class Inventory {

    private InventoryId inventoryId;
    private final ProductId productId;
    private StockQuantity quantity;
    private Long version;

    private Inventory(InventoryId inventoryId, ProductId productId, StockQuantity quantity, Long version) {
        this.inventoryId = inventoryId;
        this.productId = productId;
        this.quantity = quantity;
        this.version = version;
    }

    public static Inventory create(ProductId productId, StockQuantity quantity) {
        return new Inventory(null, productId, quantity, null);
    }

    public static Inventory restore(InventoryId inventoryId, ProductId productId, StockQuantity quantity, Long version) {
        return new Inventory(inventoryId, productId, quantity, version);
    }

    public void decrease(int amount) {
        this.quantity = this.quantity.decrease(amount);
    }

    public void increase(int amount) {
        this.quantity = this.quantity.increase(amount);
    }

    public InventoryId getInventoryId() {
        return inventoryId;
    }

    public ProductId getProductId() {
        return productId;
    }

    public StockQuantity getQuantity() {
        return quantity;
    }

    public Long getVersion() {
        return version;
    }
}
