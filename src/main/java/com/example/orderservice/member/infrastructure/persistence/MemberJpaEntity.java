package com.example.orderservice.member.infrastructure.persistence;

import com.example.orderservice.member.domain.model.Address;
import com.example.orderservice.member.domain.model.Email;
import com.example.orderservice.member.domain.model.Member;
import com.example.orderservice.member.domain.model.MemberId;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "members")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class MemberJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String name;

    private String street;
    private String detail;
    private String zipCode;

    private LocalDateTime createdAt;

    public Member toMember() {
        return Member.restore(
                new MemberId(id),
                new Email(email),
                name,
                new Address(street, detail, zipCode),
                createdAt
        );
    }

    public static MemberJpaEntity fromMember(Member member) {
        return MemberJpaEntity.builder()
                .id(member.getMemberId() != null ? member.getMemberId().getId() : null)
                .email(member.getEmail().getValue())
                .name(member.getName())
                .street(member.getAddress().getStreet())
                .detail(member.getAddress().getDetail())
                .zipCode(member.getAddress().getZipCode())
                .createdAt(member.getCreatedAt())
                .build();
    }
}
