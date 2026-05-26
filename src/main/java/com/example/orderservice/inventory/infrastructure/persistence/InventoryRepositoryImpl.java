package com.example.orderservice.inventory.infrastructure.persistence;

import com.example.orderservice.inventory.domain.model.Inventory;
import com.example.orderservice.inventory.domain.repository.InventoryRepository;
import com.example.orderservice.product.domain.model.ProductId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class InventoryRepositoryImpl implements InventoryRepository {

    private final InventoryJpaRepository inventoryJpaRepository;

    @Override
    public Inventory save(Inventory inventory) {
        InventoryJpaEntity entity = InventoryJpaEntity.fromInventory(inventory);
        InventoryJpaEntity saved = inventoryJpaRepository.save(entity);
        return saved.toInventory();
    }

    @Override
    public Optional<Inventory> findByProductId(ProductId productId) {
        return inventoryJpaRepository.findByProductId(productId.getId())
                .map(InventoryJpaEntity::toInventory);
    }
}
