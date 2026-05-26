package com.example.orderservice.order.application;

import com.example.orderservice.common.exception.MemberNotFoundException;
import com.example.orderservice.common.exception.OrderNotFoundException;
import com.example.orderservice.common.exception.ProductNotFoundException;
import com.example.orderservice.common.vo.Money;
import com.example.orderservice.member.domain.model.MemberId;
import com.example.orderservice.member.domain.repository.MemberRepository;
import com.example.orderservice.order.domain.event.OrderCreatedEvent;
import com.example.orderservice.order.domain.model.Order;
import com.example.orderservice.order.domain.model.OrderId;
import com.example.orderservice.order.domain.model.OrderItem;
import com.example.orderservice.order.domain.repository.OrderRepository;
import com.example.orderservice.order.domain.service.OrderDomainService;
import com.example.orderservice.product.domain.model.Product;
import com.example.orderservice.product.domain.model.ProductId;
import com.example.orderservice.product.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderApplicationService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final OrderDomainService orderDomainService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public OrderResponse createOrder(CreateOrderCommand command) {
        // 1. 회원 존재 확인
        MemberId memberId = new MemberId(command.getMemberId());
        memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(command.getMemberId()));

        // 2. 상품 조회
        List<Product> products = new ArrayList<>();
        for (CreateOrderCommand.OrderItemCommand itemCmd : command.getItems()) {
            ProductId productId = new ProductId(itemCmd.getProductId());
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new ProductNotFoundException(itemCmd.getProductId()));
            products.add(product);
        }

        // 3. 주문 가능 여부 검증
        orderDomainService.validateOrderable(products);

        // 4. OrderItem 목록 생성
        List<OrderItem> orderItems = new ArrayList<>();
        for (int i = 0; i < command.getItems().size(); i++) {
            CreateOrderCommand.OrderItemCommand itemCmd = command.getItems().get(i);
            Product product = products.get(i);
            OrderItem orderItem = OrderItem.of(
                    product.getProductId(),
                    product.getName(),
                    product.getPrice(),
                    itemCmd.getQuantity()
            );
            orderItems.add(orderItem);
        }

        // 5. 주문 생성
        Order order = Order.create(memberId, orderItems);

        // 6. 저장
        Order saved = orderRepository.save(order);

        // 7. 이벤트 발행
        List<OrderCreatedEvent.OrderItemInfo> itemInfos = saved.getOrderItems().stream()
                .map(item -> new OrderCreatedEvent.OrderItemInfo(
                        item.getProductId().getId(),
                        item.getProductName(),
                        item.getQuantity()
                ))
                .collect(Collectors.toList());
        eventPublisher.publishEvent(new OrderCreatedEvent(saved.getOrderId().getId(), itemInfos));

        // 8. 응답 반환
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public OrderResponse findById(Long orderId) {
        Order order = orderRepository.findById(new OrderId(orderId))
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        return toResponse(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> findByMemberId(Long memberId) {
        List<Order> orders = orderRepository.findByMemberId(new MemberId(memberId));
        return orders.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public OrderResponse cancelOrder(Long orderId) {
        Order order = orderRepository.findById(new OrderId(orderId))
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        order.cancel();
        Order saved = orderRepository.save(order);
        return toResponse(saved);
    }

    private OrderResponse toResponse(Order order) {
        List<OrderResponse.OrderItemResponse> itemResponses = order.getOrderItems().stream()
                .map(item -> OrderResponse.OrderItemResponse.builder()
                        .productId(item.getProductId().getId())
                        .productName(item.getProductName())
                        .unitPrice(item.getUnitPrice().getAmount())
                        .quantity(item.getQuantity())
                        .subtotal(item.subtotal().getAmount())
                        .build())
                .collect(Collectors.toList());

        return OrderResponse.builder()
                .orderId(order.getOrderId() != null ? order.getOrderId().getId() : null)
                .memberId(order.getMemberId().getId())
                .items(itemResponses)
                .totalAmount(order.getTotalAmount().getAmount())
                .status(order.getStatus().name())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
}
