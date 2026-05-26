package com.example.orderservice.member.application;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MemberResponse {
    private final Long memberId;
    private final String email;
    private final String name;
    private final String street;
    private final String detail;
    private final String zipCode;
    private final LocalDateTime createdAt;
}
