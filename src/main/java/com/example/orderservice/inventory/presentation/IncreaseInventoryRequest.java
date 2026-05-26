package com.example.orderservice.inventory.presentation;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class IncreaseInventoryRequest {

    @Min(1)
    private int amount;
}
