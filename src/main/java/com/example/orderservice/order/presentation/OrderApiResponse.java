package com.example.orderservice.order.presentation;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class OrderApiResponse {

    private final Long orderId;
    private final Long memberId;
    private final List<OrderItemApiResponse> items;
    private final BigDecimal totalAmount;
    private final String status;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    @Getter
    @Builder
    public static class OrderItemApiResponse {
        private final Long productId;
        private final String productName;
        private final BigDecimal unitPrice;
        private final int quantity;
        private final BigDecimal subtotal;
    }
}
