package com.example.orderservice.inventory.infrastructure.persistence;

import com.example.orderservice.inventory.domain.model.Inventory;
import com.example.orderservice.inventory.domain.model.InventoryId;
import com.example.orderservice.inventory.domain.model.StockQuantity;
import com.example.orderservice.product.domain.model.ProductId;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "inventories")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class InventoryJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long productId;

    @Column(nullable = false)
    private int quantity;

    @Version
    private Long version;

    public Inventory toInventory() {
        return Inventory.restore(
                new InventoryId(id),
                new ProductId(productId),
                new StockQuantity(quantity),
                version
        );
    }

    public static InventoryJpaEntity fromInventory(Inventory inventory) {
        return InventoryJpaEntity.builder()
                .id(inventory.getInventoryId() != null ? inventory.getInventoryId().getId() : null)
                .productId(inventory.getProductId().getId())
                .quantity(inventory.getQuantity().getValue())
                .version(inventory.getVersion())
                .build();
    }
}
