package com.example.orderservice.common.vo;

import java.math.BigDecimal;
import java.util.Objects;

public final class Money {

    private final BigDecimal amount;

    private Money(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Money amount cannot be null");
        }
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Money amount cannot be negative");
        }
        this.amount = amount;
    }

    public static Money of(BigDecimal amount) {
        return new Money(amount);
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Money add(Money other) {
        if (other == null) {
            throw new IllegalArgumentException("Other money cannot be null");
        }
        return new Money(this.amount.add(other.amount));
    }

    public Money multiply(int multiplier) {
        if (multiplier < 0) {
            throw new IllegalArgumentException("Multiplier cannot be negative");
        }
        return new Money(this.amount.multiply(BigDecimal.valueOf(multiplier)));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Money money = (Money) o;
        return amount.compareTo(money.amount) == 0;
    }

    @Override
    public int hashCode() {
        // scale 정규화 후 hashCode
        return amount.stripTrailingZeros().hashCode();
    }

    @Override
    public String toString() {
        return "Money{amount=" + amount + "}";
    }
}
