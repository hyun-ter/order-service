package com.example.orderservice.inventory.domain.model;

import com.example.orderservice.common.exception.OutOfStockException;

import java.util.Objects;

/**
 * 재고 수량을 나타내는 Value Object.
 *
 * <p>음수 수량을 허용하지 않는 도메인 규칙을 캡슐화한다.
 * 불변(immutable) 객체이며, decrease/increase 연산은 항상 새 인스턴스를 반환한다.
 */
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

    /**
     * 재고를 차감한다.
     * 차감 후 수량이 0 미만이면 재고 부족으로 판단하고 예외를 던진다.
     *
     * @throws OutOfStockException 차감 후 수량이 0 미만인 경우
     */
    public StockQuantity decrease(int amount) {
        int newValue = this.value - amount;
        if (newValue < 0) {
            throw new OutOfStockException("재고 부족: 현재=" + this.value + ", 요청=" + amount);
        }
        return new StockQuantity(newValue);
    }

    /** 재고를 증가시킨 새 StockQuantity를 반환한다. */
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
