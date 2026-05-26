package com.example.orderservice.seller.presentation;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RegisterSellerRequest {

    @NotBlank
    private String businessName;

    @NotBlank
    private String phone;

    @NotBlank
    private String email;
}
