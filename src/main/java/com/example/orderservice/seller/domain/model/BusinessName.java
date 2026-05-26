package com.example.orderservice.seller.domain.model;

import java.util.Objects;

public final class BusinessName {

    private final String value;

    public BusinessName(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("BusinessName cannot be null or blank");
        }
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BusinessName that = (BusinessName) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
