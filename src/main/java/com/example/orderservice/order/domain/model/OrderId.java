package com.example.orderservice.order.domain.model;

import java.util.Objects;

public final class OrderId {

    private final Long id;

    public OrderId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("OrderId cannot be null");
        }
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderId orderId = (OrderId) o;
        return Objects.equals(id, orderId.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
