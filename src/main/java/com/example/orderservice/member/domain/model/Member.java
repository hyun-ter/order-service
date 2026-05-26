package com.example.orderservice.member.domain.model;

import java.time.LocalDateTime;

public class Member {

    private final MemberId memberId;
    private final Email email;
    private final String name;
    private final Address address;
    private final LocalDateTime createdAt;

    private Member(MemberId memberId, Email email, String name, Address address, LocalDateTime createdAt) {
        this.memberId = memberId;
        this.email = email;
        this.name = name;
        this.address = address;
        this.createdAt = createdAt;
    }

    public static Member register(Email email, String name, Address address) {
        return new Member(null, email, name, address, LocalDateTime.now());
    }

    public static Member restore(MemberId memberId, Email email, String name, Address address, LocalDateTime createdAt) {
        return new Member(memberId, email, name, address, createdAt);
    }

    public MemberId getMemberId() {
        return memberId;
    }

    public Email getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public Address getAddress() {
        return address;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
