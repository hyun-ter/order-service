package com.example.orderservice.order.infrastructure.persistence;

import com.example.orderservice.common.vo.Money;
import com.example.orderservice.member.domain.model.MemberId;
import com.example.orderservice.order.domain.model.Order;
import com.example.orderservice.order.domain.model.OrderId;
import com.example.orderservice.order.domain.model.OrderItem;
import com.example.orderservice.order.domain.model.OrderStatus;
import com.example.orderservice.product.domain.model.ProductId;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class OrderJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long memberId;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItemJpaEntity> orderItems = new ArrayList<>();

    public Order toOrder() {
        List<OrderItem> domainItems = orderItems.stream()
                .map(item -> OrderItem.of(
                        new ProductId(item.getProductId()),
                        item.getProductName(),
                        Money.of(item.getUnitPrice()),
                        item.getQuantity()
                ))
                .collect(Collectors.toList());

        return Order.restore(
                new OrderId(id),
                new MemberId(memberId),
                domainItems,
                Money.of(totalAmount),
                status,
                createdAt,
                updatedAt
        );
    }

    public static OrderJpaEntity fromOrder(Order order) {
        OrderJpaEntity entity = OrderJpaEntity.builder()
                .id(order.getOrderId() != null ? order.getOrderId().getId() : null)
                .memberId(order.getMemberId().getId())
                .totalAmount(order.getTotalAmount().getAmount())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();

        List<OrderItemJpaEntity> itemEntities = order.getOrderItems().stream()
                .map(item -> OrderItemJpaEntity.builder()
                        .order(entity)
                        .productId(item.getProductId().getId())
                        .productName(item.getProductName())
                        .unitPrice(item.getUnitPrice().getAmount())
                        .quantity(item.getQuantity())
                        .build())
                .collect(Collectors.toList());

        entity.orderItems.addAll(itemEntities);

        return entity;
    }
}
