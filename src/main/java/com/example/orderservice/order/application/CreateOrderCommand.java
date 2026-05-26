package com.example.orderservice.order.application;

import java.util.List;

public class CreateOrderCommand {

    private Long memberId;
    private List<OrderItemCommand> items;

    public CreateOrderCommand() {
    }

    public CreateOrderCommand(Long memberId, List<OrderItemCommand> items) {
        this.memberId = memberId;
        this.items = items;
    }

    public Long getMemberId() {
        return memberId;
    }

    public List<OrderItemCommand> getItems() {
        return items;
    }

    public static class OrderItemCommand {
        private Long productId;
        private int quantity;

        public OrderItemCommand() {
        }

        public OrderItemCommand(Long productId, int quantity) {
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
}
