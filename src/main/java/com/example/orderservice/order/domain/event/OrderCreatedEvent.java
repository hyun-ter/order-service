package com.example.orderservice.order.domain.event;

import java.util.List;

public class OrderCreatedEvent {

    private final Long orderId;
    private final List<OrderItemInfo> orderItems;

    public OrderCreatedEvent(Long orderId, List<OrderItemInfo> orderItems) {
        this.orderId = orderId;
        this.orderItems = orderItems;
    }

    public Long getOrderId() {
        return orderId;
    }

    public List<OrderItemInfo> getOrderItems() {
        return orderItems;
    }

    public static class OrderItemInfo {
        private final Long productId;
        private final String productName;
        private final int quantity;

        public OrderItemInfo(Long productId, String productName, int quantity) {
            this.productId = productId;
            this.productName = productName;
            this.quantity = quantity;
        }

        public Long getProductId() {
            return productId;
        }

        public String getProductName() {
            return productName;
        }

        public int getQuantity() {
            return quantity;
        }
    }
}
