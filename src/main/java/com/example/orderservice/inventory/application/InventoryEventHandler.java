package com.example.orderservice.inventory.application;

import com.example.orderservice.common.exception.InventoryNotFoundException;
import com.example.orderservice.inventory.domain.model.Inventory;
import com.example.orderservice.inventory.domain.repository.InventoryRepository;
import com.example.orderservice.order.domain.event.OrderCreatedEvent;
import com.example.orderservice.product.domain.model.ProductId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class InventoryEventHandler {

    private final InventoryRepository inventoryRepository;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    @Transactional(propagation = Propagation.MANDATORY)
    public void handleOrderCreated(OrderCreatedEvent event) {
        for (OrderCreatedEvent.OrderItemInfo item : event.getOrderItems()) {
            ProductId productId = new ProductId(item.getProductId());
            Inventory inventory = inventoryRepository.findByProductId(productId)
                    .orElseThrow(() -> new InventoryNotFoundException("재고 없음: productId=" + item.getProductId()));
            inventory.decrease(item.getQuantity());
            inventoryRepository.save(inventory);
        }
    }
}
