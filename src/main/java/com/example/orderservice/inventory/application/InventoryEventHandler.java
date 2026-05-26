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

/**
 * 주문 생성 이벤트를 수신하여 재고를 차감하는 이벤트 핸들러.
 *
 * <p>주문 서비스와 재고 서비스 간의 느슨한 결합(Loose Coupling)을 구현한다.
 * OrderApplicationService가 직접 InventoryService를 호출하지 않고
 * 이벤트를 통해 재고 차감이 이루어진다.
 *
 * <h3>트랜잭션 설계</h3>
 * <ul>
 *   <li>{@code BEFORE_COMMIT}: 주문 저장 트랜잭션이 커밋되기 직전에 실행된다.</li>
 *   <li>{@code Propagation.MANDATORY}: 반드시 기존 트랜잭션에 참여한다.
 *       주문 트랜잭션과 동일한 트랜잭션 내에서 재고를 차감하므로,
 *       재고 부족 시 {@code OutOfStockException}이 발생하면 주문 저장까지 함께 롤백된다.</li>
 * </ul>
 */
@Component
@RequiredArgsConstructor
public class InventoryEventHandler {

    private final InventoryRepository inventoryRepository;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    @Transactional(propagation = Propagation.MANDATORY)
    public void handleOrderCreated(OrderCreatedEvent event) {
        for (OrderCreatedEvent.OrderItemInfo item : event.getOrderItems()) {
            ProductId productId = new ProductId(item.getProductId());

            // 해당 상품의 재고 조회 — 재고 정보가 없으면 예외 발생
            Inventory inventory = inventoryRepository.findByProductId(productId)
                    .orElseThrow(() -> new InventoryNotFoundException("재고 없음: productId=" + item.getProductId()));

            // 재고 차감 — StockQuantity.decrease()에서 재고 부족 시 OutOfStockException 발생
            inventory.decrease(item.getQuantity());
            inventoryRepository.save(inventory);
        }
    }
}
