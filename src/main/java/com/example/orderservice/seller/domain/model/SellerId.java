package com.example.orderservice.seller.domain.model;

import java.util.Objects;

public final class SellerId {

    private final Long id;

    public SellerId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("SellerId cannot be null");
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
        SellerId sellerId = (SellerId) o;
        return Objects.equals(id, sellerId.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
