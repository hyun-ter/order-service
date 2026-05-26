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

/**
 * 주문 Application Service.
 *
 * <p>주문 생성 플로우를 오케스트레이션한다:
 * 회원/상품 조회 → 주문 가능 검증 → Order 생성 → 저장 → 이벤트 발행
 *
 * <p>이벤트 발행 후 {@code InventoryEventHandler}가 {@code OrderCreatedEvent}를 수신하여
 * 같은 트랜잭션 내에서(BEFORE_COMMIT) 재고를 차감한다.
 * 재고 부족 시 {@code OutOfStockException}이 발생하고 트랜잭션 전체가 롤백된다.
 */
@Service
@RequiredArgsConstructor
public class OrderApplicationService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final OrderDomainService orderDomainService;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 주문을 생성한다.
     *
     * <ol>
     *   <li>회원 존재 확인</li>
     *   <li>상품 목록 조회 및 ON_SALE 상태 검증</li>
     *   <li>OrderItem 생성 (주문 시점 상품명·단가 스냅샷)</li>
     *   <li>Order 도메인 객체 생성 및 저장</li>
     *   <li>OrderCreatedEvent 발행 → InventoryEventHandler가 재고 차감</li>
     * </ol>
     */
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

        // 3. 주문 가능 여부 검증 (ON_SALE 상태인지 확인)
        orderDomainService.validateOrderable(products);

        // 4. OrderItem 목록 생성 — 상품명·단가를 현재 시점으로 스냅샷
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

        // 5. 주문 생성 (status=PENDING, totalAmount 자동 계산)
        Order order = Order.create(memberId, orderItems);

        // 6. 저장
        Order saved = orderRepository.save(order);

        // 7. 이벤트 발행 — InventoryEventHandler가 BEFORE_COMMIT 단계에서 재고 차감
        //    재고 부족 시 OutOfStockException 발생 → 트랜잭션 롤백으로 주문도 취소됨
        List<OrderCreatedEvent.OrderItemInfo> itemInfos = saved.getOrderItems().stream()
                .map(item -> new OrderCreatedEvent.OrderItemInfo(
                        item.getProductId().getId(),
                        item.getProductName(),
                        item.getQuantity()
                ))
                .collect(Collectors.toList());
        eventPublisher.publishEvent(new OrderCreatedEvent(saved.getOrderId().getId(), itemInfos));

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
