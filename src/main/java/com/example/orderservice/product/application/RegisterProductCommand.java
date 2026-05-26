package com.example.orderservice.product.application;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class RegisterProductCommand {

    private final Long sellerId;
    private final String name;
    private final BigDecimal price;
    private final String category;
}
