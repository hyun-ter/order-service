package com.example.orderservice.product.domain.model;

import java.util.Objects;

public final class ProductId {

    private final Long id;

    public ProductId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ProductId cannot be null");
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
        ProductId productId = (ProductId) o;
        return Objects.equals(id, productId.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
