package com.example.orderservice.inventory.domain.repository;

import com.example.orderservice.inventory.domain.model.Inventory;
import com.example.orderservice.product.domain.model.ProductId;

import java.util.Optional;

public interface InventoryRepository {

    Inventory save(Inventory inventory);

    Optional<Inventory> findByProductId(ProductId productId);
}
