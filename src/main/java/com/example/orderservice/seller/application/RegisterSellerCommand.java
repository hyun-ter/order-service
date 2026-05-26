package com.example.orderservice.seller.application;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RegisterSellerCommand {

    private final String businessName;
    private final String phone;
    private final String email;
}
