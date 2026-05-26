package com.example.orderservice.common.exception;

public class MemberNotFoundException extends RuntimeException {

    public MemberNotFoundException(Long memberId) {
        super("Member not found: " + memberId);
    }
}
