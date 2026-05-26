package com.example.orderservice.inventory.domain.model;

import java.util.Objects;

public final class InventoryId {

    private final Long id;

    public InventoryId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("InventoryId cannot be null");
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
        InventoryId that = (InventoryId) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
