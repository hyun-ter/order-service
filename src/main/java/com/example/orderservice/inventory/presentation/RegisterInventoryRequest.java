package com.example.orderservice.inventory.presentation;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RegisterInventoryRequest {

    @NotNull
    private Long productId;

    @Min(0)
    private int quantity;
}
