package com.example.orderservice.inventory.domain.event;

public class OutOfStockEvent {

    private final Long productId;
    private final int requestedQuantity;

    public OutOfStockEvent(Long productId, int requestedQuantity) {
        this.productId = productId;
        this.requestedQuantity = requestedQuantity;
    }

    public Long getProductId() {
        return productId;
    }

    public int getRequestedQuantity() {
        return requestedQuantity;
    }
}
