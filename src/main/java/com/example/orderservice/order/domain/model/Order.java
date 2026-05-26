package com.example.orderservice.order.domain.model;

import com.example.orderservice.common.exception.InvalidOrderStatusException;
import com.example.orderservice.common.vo.Money;
import com.example.orderservice.member.domain.model.MemberId;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Order {

    private OrderId orderId;
    private final MemberId memberId;
    private final List<OrderItem> orderItems;
    private Money totalAmount;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Order(OrderId orderId, MemberId memberId, List<OrderItem> orderItems,
                  Money totalAmount, OrderStatus status,
                  LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.orderId = orderId;
        this.memberId = memberId;
        this.orderItems = new ArrayList<>(orderItems);
        this.totalAmount = totalAmount;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Order create(MemberId memberId, List<OrderItem> orderItems) {
        if (memberId == null) throw new IllegalArgumentException("MemberId cannot be null");
        if (orderItems == null || orderItems.isEmpty()) throw new IllegalArgumentException("Order items cannot be empty");

        Money totalAmount = orderItems.stream()
                .map(OrderItem::subtotal)
                .reduce(Money.of(BigDecimal.ZERO), Money::add);

        LocalDateTime now = LocalDateTime.now();
        return new Order(null, memberId, orderItems, totalAmount, OrderStatus.PENDING, now, now);
    }

    public static Order restore(OrderId orderId, MemberId memberId, List<OrderItem> orderItems,
                                Money totalAmount, OrderStatus status,
                                LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new Order(orderId, memberId, orderItems, totalAmount, status, createdAt, updatedAt);
    }

    public void cancel() {
        if (this.status != OrderStatus.PENDING) {
            throw new InvalidOrderStatusException("주문 취소는 PENDING 상태에서만 가능합니다. 현재 상태: " + this.status);
        }
        this.status = OrderStatus.CANCELLED;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateStatus(OrderStatus newStatus) {
        this.status = newStatus;
        this.updatedAt = LocalDateTime.now();
    }

    public OrderId getOrderId() {
        return orderId;
    }

    public MemberId getMemberId() {
        return memberId;
    }

    public List<OrderItem> getOrderItems() {
        return Collections.unmodifiableList(orderItems);
    }

    public Money getTotalAmount() {
        return totalAmount;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
