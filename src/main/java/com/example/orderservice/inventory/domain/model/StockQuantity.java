package com.example.orderservice.inventory.domain.model;

import com.example.orderservice.common.exception.OutOfStockException;

import java.util.Objects;

public final class StockQuantity {

    private final int value;

    public StockQuantity(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("재고 수량은 0 이상이어야 합니다: " + value);
        }
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public StockQuantity decrease(int amount) {
        int newValue = this.value - amount;
        if (newValue < 0) {
            throw new OutOfStockException("재고 부족: 현재=" + this.value + ", 요청=" + amount);
        }
        return new StockQuantity(newValue);
    }

    public StockQuantity increase(int amount) {
        return new StockQuantity(this.value + amount);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StockQuantity that = (StockQuantity) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
