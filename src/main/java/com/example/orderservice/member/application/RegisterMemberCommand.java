package com.example.orderservice.member.application;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RegisterMemberCommand {
    private final String email;
    private final String name;
    private final String street;
    private final String detail;
    private final String zipCode;
}
