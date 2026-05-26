package com.example.orderservice.order.infrastructure.persistence;

import com.example.orderservice.member.domain.model.MemberId;
import com.example.orderservice.order.domain.model.Order;
import com.example.orderservice.order.domain.model.OrderId;
import com.example.orderservice.order.domain.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {

    private final OrderJpaRepository orderJpaRepository;

    @Override
    public Order save(Order order) {
        OrderJpaEntity entity = OrderJpaEntity.fromOrder(order);
        OrderJpaEntity saved = orderJpaRepository.save(entity);
        return saved.toOrder();
    }

    @Override
    public Optional<Order> findById(OrderId orderId) {
        return orderJpaRepository.findById(orderId.getId())
                .map(OrderJpaEntity::toOrder);
    }

    @Override
    public List<Order> findByMemberId(MemberId memberId) {
        return orderJpaRepository.findByMemberId(memberId.getId()).stream()
                .map(OrderJpaEntity::toOrder)
                .collect(Collectors.toList());
    }
}
