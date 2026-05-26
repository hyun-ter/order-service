package com.example.orderservice.inventory.application;

public class RegisterInventoryCommand {

    private final Long productId;
    private final int quantity;

    public RegisterInventoryCommand(Long productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public Long getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }
}
