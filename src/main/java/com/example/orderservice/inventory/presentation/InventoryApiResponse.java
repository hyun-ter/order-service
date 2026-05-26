package com.example.orderservice.inventory.presentation;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class InventoryApiResponse {

    private final Long inventoryId;
    private final Long productId;
    private final int quantity;
}
