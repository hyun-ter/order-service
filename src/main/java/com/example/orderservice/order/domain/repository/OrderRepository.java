package com.example.orderservice.order.domain.repository;

import com.example.orderservice.member.domain.model.MemberId;
import com.example.orderservice.order.domain.model.Order;
import com.example.orderservice.order.domain.model.OrderId;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {

    Order save(Order order);

    Optional<Order> findById(OrderId orderId);

    List<Order> findByMemberId(MemberId memberId);
}
