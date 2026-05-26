package com.example.orderservice.inventory.application;

import com.example.orderservice.common.exception.InventoryNotFoundException;
import com.example.orderservice.inventory.domain.model.Inventory;
import com.example.orderservice.inventory.domain.model.StockQuantity;
import com.example.orderservice.inventory.domain.repository.InventoryRepository;
import com.example.orderservice.product.domain.model.ProductId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InventoryApplicationService {

    private final InventoryRepository inventoryRepository;

    @Transactional
    public InventoryResponse register(RegisterInventoryCommand command) {
        ProductId productId = new ProductId(command.getProductId());
        StockQuantity quantity = new StockQuantity(command.getQuantity());
        Inventory inventory = Inventory.create(productId, quantity);
        Inventory saved = inventoryRepository.save(inventory);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public InventoryResponse findByProductId(Long productId) {
        ProductId pid = new ProductId(productId);
        Inventory inventory = inventoryRepository.findByProductId(pid)
                .orElseThrow(() -> new InventoryNotFoundException(productId));
        return toResponse(inventory);
    }

    @Transactional
    public InventoryResponse increaseStock(Long productId, int amount) {
        ProductId pid = new ProductId(productId);
        Inventory inventory = inventoryRepository.findByProductId(pid)
                .orElseThrow(() -> new InventoryNotFoundException(productId));
        inventory.increase(amount);
        Inventory saved = inventoryRepository.save(inventory);
        return toResponse(saved);
    }

    private InventoryResponse toResponse(Inventory inventory) {
        return InventoryResponse.builder()
                .inventoryId(inventory.getInventoryId() != null ? inventory.getInventoryId().getId() : null)
                .productId(inventory.getProductId().getId())
                .quantity(inventory.getQuantity().getValue())
                .build();
    }
}
