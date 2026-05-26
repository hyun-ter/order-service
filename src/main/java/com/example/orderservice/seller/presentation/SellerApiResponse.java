package com.example.orderservice.seller.presentation;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class SellerApiResponse {

    private final Long sellerId;
    private final String businessName;
    private final String phone;
    private final String email;
    private final String status;
    private final LocalDateTime createdAt;
}
