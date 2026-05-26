package com.example.orderservice.order.presentation;

import com.example.orderservice.order.application.CreateOrderCommand;
import com.example.orderservice.order.application.OrderApplicationService;
import com.example.orderservice.order.application.OrderResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderApplicationService orderApplicationService;

    @PostMapping
    public ResponseEntity<OrderApiResponse> createOrder(@RequestBody @Valid CreateOrderRequest request) {
        List<CreateOrderCommand.OrderItemCommand> itemCommands = request.getItems().stream()
                .map(item -> new CreateOrderCommand.OrderItemCommand(item.getProductId(), item.getQuantity()))
                .collect(Collectors.toList());

        CreateOrderCommand command = new CreateOrderCommand(request.getMemberId(), itemCommands);
        OrderResponse response = orderApplicationService.createOrder(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(toApiResponse(response));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderApiResponse> findById(@PathVariable Long orderId) {
        OrderResponse response = orderApplicationService.findById(orderId);
        return ResponseEntity.ok(toApiResponse(response));
    }

    @GetMapping
    public ResponseEntity<List<OrderApiResponse>> findByMemberId(@RequestParam Long memberId) {
        List<OrderResponse> responses = orderApplicationService.findByMemberId(memberId);
        List<OrderApiResponse> apiResponses = responses.stream()
                .map(this::toApiResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(apiResponses);
    }

    @PatchMapping("/{orderId}/cancel")
    public ResponseEntity<OrderApiResponse> cancelOrder(@PathVariable Long orderId) {
        OrderResponse response = orderApplicationService.cancelOrder(orderId);
        return ResponseEntity.ok(toApiResponse(response));
    }

    private OrderApiResponse toApiResponse(OrderResponse response) {
        List<OrderApiResponse.OrderItemApiResponse> itemApiResponses = response.getItems().stream()
                .map(item -> OrderApiResponse.OrderItemApiResponse.builder()
                        .productId(item.getProductId())
                        .productName(item.getProductName())
                        .unitPrice(item.getUnitPrice())
                        .quantity(item.getQuantity())
                        .subtotal(item.getSubtotal())
                        .build())
                .collect(Collectors.toList());

        return OrderApiResponse.builder()
                .orderId(response.getOrderId())
                .memberId(response.getMemberId())
                .items(itemApiResponses)
                .totalAmount(response.getTotalAmount())
                .status(response.getStatus())
                .createdAt(response.getCreatedAt())
                .updatedAt(response.getUpdatedAt())
                .build();
    }
}
