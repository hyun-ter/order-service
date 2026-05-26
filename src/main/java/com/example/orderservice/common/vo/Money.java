package com.example.orderservice.common.vo;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * 금액을 나타내는 공유 Value Object.
 *
 * <p>float/double 대신 {@link BigDecimal}을 사용해 금융 계산의 정밀도를 보장한다.
 * 불변(immutable) 객체이므로 add/multiply 연산은 항상 새 인스턴스를 반환한다.
 *
 * <p>equals 비교 시 scale 차이를 무시한다 (1.0 == 1.00).
 * {@link BigDecimal#equals}는 scale까지 비교하므로 {@link BigDecimal#compareTo}를 사용한다.
 */
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

    /** 금액 값으로 Money 인스턴스를 생성한다. */
    public static Money of(BigDecimal amount) {
        return new Money(amount);
    }

    public BigDecimal getAmount() {
        return amount;
    }

    /** 두 금액을 더한 새 Money를 반환한다. */
    public Money add(Money other) {
        if (other == null) {
            throw new IllegalArgumentException("Other money cannot be null");
        }
        return new Money(this.amount.add(other.amount));
    }

    /** 금액에 정수 배율을 곱한 새 Money를 반환한다. 주문 항목의 소계 계산에 사용된다. */
    public Money multiply(int multiplier) {
        if (multiplier < 0) {
            throw new IllegalArgumentException("Multiplier cannot be negative");
        }
        return new Money(this.amount.multiply(BigDecimal.valueOf(multiplier)));
    }

    /**
     * scale에 관계없이 금액 값이 같으면 동등하다고 판단한다.
     * BigDecimal.equals()는 1.0과 1.00을 다르게 취급하므로 compareTo를 사용한다.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Money money = (Money) o;
        return amount.compareTo(money.amount) == 0;
    }

    @Override
    public int hashCode() {
        // equals와 일관성을 맞추기 위해 scale을 제거한 뒤 hashCode를 계산한다
        return amount.stripTrailingZeros().hashCode();
    }

    @Override
    public String toString() {
        return "Money{amount=" + amount + "}";
    }
}
