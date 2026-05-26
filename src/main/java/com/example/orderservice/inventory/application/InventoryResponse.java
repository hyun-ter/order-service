package com.example.orderservice.inventory.application;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class InventoryResponse {

    private final Long inventoryId;
    private final Long productId;
    private final int quantity;
}
