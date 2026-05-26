package com.example.orderservice.common.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MoneyTest {

    @Test
    @DisplayName("BigDecimal로 Money 정상 생성")
    void of_정상생성() {
        Money money = Money.of(new BigDecimal("1000"));
        assertThat(money.getAmount()).isEqualByComparingTo(new BigDecimal("1000"));
    }

    @Test
    @DisplayName("null 값으로 생성 시 예외 발생")
    void of_null이면_예외() {
        assertThatThrownBy(() -> Money.of(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be null");
    }

    @Test
    @DisplayName("음수 값으로 생성 시 예외 발생")
    void of_음수면_예외() {
        assertThatThrownBy(() -> Money.of(new BigDecimal("-1")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be negative");
    }

    @Test
    @DisplayName("add() 정상 동작")
    void add_정상동작() {
        Money a = Money.of(new BigDecimal("1000"));
        Money b = Money.of(new BigDecimal("500"));
        Money result = a.add(b);
        assertThat(result.getAmount()).isEqualByComparingTo(new BigDecimal("1500"));
    }

    @Test
    @DisplayName("multiply(int) 정상 동작")
    void multiply_정상동작() {
        Money money = Money.of(new BigDecimal("100"));
        Money result = money.multiply(3);
        assertThat(result.getAmount()).isEqualByComparingTo(new BigDecimal("300"));
    }

    @Test
    @DisplayName("equals: scale이 달라도 같은 금액이면 equal")
    void equals_scale이달라도같으면equal() {
        Money a = Money.of(new BigDecimal("1.0"));
        Money b = Money.of(new BigDecimal("1.00"));
        assertThat(a).isEqualTo(b);
    }
}
