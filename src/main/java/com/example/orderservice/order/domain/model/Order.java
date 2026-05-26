package com.example.orderservice.order.domain.model;

import com.example.orderservice.common.exception.InvalidOrderStatusException;
import com.example.orderservice.common.vo.Money;
import com.example.orderservice.member.domain.model.MemberId;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 주문 Aggregate Root.
 *
 * <p>주문 도메인의 일관성 경계(Consistency Boundary)를 책임진다.
 * 주문 항목({@link OrderItem}) 컬렉션과 총 금액, 상태를 함께 관리한다.
 *
 * <p>상태 전이: PENDING → PAID → SHIPPING → DELIVERED / CANCELLED
 * PENDING 상태에서만 취소가 가능하다.
 *
 * <p>이벤트 발행: {@link #create} 이후 Application Service에서
 * {@code ApplicationEventPublisher}를 통해 {@code OrderCreatedEvent}를 발행한다.
 */
public class Order {

    private OrderId orderId;
    private final MemberId memberId;
    private final List<OrderItem> orderItems;
    private Money totalAmount;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Order(OrderId orderId, MemberId memberId, List<OrderItem> orderItems,
                  Money totalAmount, OrderStatus status,
                  LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.orderId = orderId;
        this.memberId = memberId;
        this.orderItems = new ArrayList<>(orderItems);
        this.totalAmount = totalAmount;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * 새 주문을 생성하는 팩토리 메서드.
     * 각 항목의 소계를 합산하여 totalAmount를 계산하고 상태를 PENDING으로 초기화한다.
     * orderId는 null로 시작하며 JPA 저장 후 ID가 부여된다.
     */
    public static Order create(MemberId memberId, List<OrderItem> orderItems) {
        if (memberId == null) throw new IllegalArgumentException("MemberId cannot be null");
        if (orderItems == null || orderItems.isEmpty()) throw new IllegalArgumentException("Order items cannot be empty");

        Money totalAmount = orderItems.stream()
                .map(OrderItem::subtotal)
                .reduce(Money.of(BigDecimal.ZERO), Money::add);

        LocalDateTime now = LocalDateTime.now();
        return new Order(null, memberId, orderItems, totalAmount, OrderStatus.PENDING, now, now);
    }

    /**
     * DB에서 조회한 데이터로 주문 객체를 재구성하는 팩토리 메서드.
     * Infrastructure 레이어(OrderRepositoryImpl)에서만 호출한다.
     */
    public static Order restore(OrderId orderId, MemberId memberId, List<OrderItem> orderItems,
                                Money totalAmount, OrderStatus status,
                                LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new Order(orderId, memberId, orderItems, totalAmount, status, createdAt, updatedAt);
    }

    /**
     * 주문을 취소한다.
     * PENDING 상태에서만 취소 가능하며, 이미 결제된 주문은 취소할 수 없다.
     *
     * @throws InvalidOrderStatusException PENDING이 아닌 상태에서 취소 시도 시
     */
    public void cancel() {
        if (this.status != OrderStatus.PENDING) {
            throw new InvalidOrderStatusException("주문 취소는 PENDING 상태에서만 가능합니다. 현재 상태: " + this.status);
        }
        this.status = OrderStatus.CANCELLED;
        this.updatedAt = LocalDateTime.now();
    }

    /** 주문 상태를 변경하고 updatedAt을 갱신한다. */
    public void updateStatus(OrderStatus newStatus) {
        this.status = newStatus;
        this.updatedAt = LocalDateTime.now();
    }

    public OrderId getOrderId() {
        return orderId;
    }

    public MemberId getMemberId() {
        return memberId;
    }

    /** 주문 항목 목록을 읽기 전용으로 반환한다. 외부에서 직접 변경할 수 없다. */
    public List<OrderItem> getOrderItems() {
        return Collections.unmodifiableList(orderItems);
    }

    public Money getTotalAmount() {
        return totalAmount;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
