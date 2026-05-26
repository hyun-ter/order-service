package com.example.orderservice.order.application;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class OrderResponse {

    private final Long orderId;
    private final Long memberId;
    private final List<OrderItemResponse> items;
    private final BigDecimal totalAmount;
    private final String status;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    @Getter
    @Builder
    public static class OrderItemResponse {
        private final Long productId;
        private final String productName;
        private final BigDecimal unitPrice;
        private final int quantity;
        private final BigDecimal subtotal;
    }
}
