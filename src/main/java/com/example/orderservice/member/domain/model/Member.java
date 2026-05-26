package com.example.orderservice.member.domain.model;

import java.time.LocalDateTime;

/**
 * 회원 Aggregate Root.
 *
 * <p>도메인 레이어에 위치하며 Spring/JPA 등 외부 의존성을 갖지 않는 순수 Java 객체다.
 * 직접 생성자 호출을 막고 {@link #register} 팩토리 메서드를 통해서만 생성한다.
 */
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

    /**
     * 새 회원을 등록할 때 사용하는 팩토리 메서드.
     * memberId는 null로 시작하며 JPA 저장 후 ID가 부여된다.
     */
    public static Member register(Email email, String name, Address address) {
        return new Member(null, email, name, address, LocalDateTime.now());
    }

    /**
     * DB에서 조회한 데이터로 도메인 객체를 재구성할 때 사용하는 팩토리 메서드.
     * Infrastructure 레이어(MemberRepositoryImpl)에서만 호출한다.
     */
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
