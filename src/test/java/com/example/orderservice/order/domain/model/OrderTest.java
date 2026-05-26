package com.example.orderservice.order.domain.model;

import com.example.orderservice.common.exception.InvalidOrderStatusException;
import com.example.orderservice.common.vo.Money;
import com.example.orderservice.member.domain.model.MemberId;
import com.example.orderservice.product.domain.model.ProductId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderTest {

    @Test
    @DisplayName("Order.create(): totalAmount 합산 검증, status=PENDING")
    void create_totalAmount합산_status_PENDING() {
        MemberId memberId = new MemberId(1L);
        OrderItem item1 = OrderItem.of(new ProductId(1L), "상품A", Money.of(new BigDecimal("1000")), 2);
        OrderItem item2 = OrderItem.of(new ProductId(2L), "상품B", Money.of(new BigDecimal("500")), 3);

        Order order = Order.create(memberId, List.of(item1, item2));

        // item1: 1000 * 2 = 2000, item2: 500 * 3 = 1500, total = 3500
        assertThat(order.getTotalAmount()).isEqualTo(Money.of(new BigDecimal("3500")));
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(order.getMemberId()).isEqualTo(memberId);
    }

    @Test
    @DisplayName("Order.cancel(): PENDING 상태에서 취소 가능")
    void cancel_PENDING상태에서_취소가능() {
        MemberId memberId = new MemberId(1L);
        OrderItem item = OrderItem.of(new ProductId(1L), "상품A", Money.of(new BigDecimal("1000")), 1);
        Order order = Order.create(memberId, List.of(item));

        order.cancel();

        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
    }

    @Test
    @DisplayName("Order.cancel(): PAID 상태에서 InvalidOrderStatusException 발생")
    void cancel_PAID상태에서_예외발생() {
        MemberId memberId = new MemberId(1L);
        OrderItem item = OrderItem.of(new ProductId(1L), "상품A", Money.of(new BigDecimal("1000")), 1);
        Order order = Order.create(memberId, List.of(item));
        order.updateStatus(OrderStatus.PAID);

        assertThatThrownBy(order::cancel)
                .isInstanceOf(InvalidOrderStatusException.class)
                .hasMessageContaining("PENDING");
    }
}
