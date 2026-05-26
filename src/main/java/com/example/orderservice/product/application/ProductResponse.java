package com.example.orderservice.product.application;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class ProductResponse {

    private final Long productId;
    private final Long sellerId;
    private final String name;
    private final BigDecimal price;
    private final String category;
    private final String status;
}
