package com.example.orderservice.member.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MemberTest {

    @Test
    @DisplayName("Member.register()로 정상 생성")
    void register_정상생성() {
        Email email = new Email("test@test.com");
        Address address = new Address("서울시 강남구", "101호", "12345");

        Member member = Member.register(email, "테스터", address);

        assertThat(member.getEmail().getValue()).isEqualTo("test@test.com");
        assertThat(member.getName()).isEqualTo("테스터");
        assertThat(member.getAddress().getStreet()).isEqualTo("서울시 강남구");
        assertThat(member.getMemberId()).isNull();
        assertThat(member.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("잘못된 이메일이면 Email 생성 시 예외 발생")
    void email_잘못된형식이면_예외() {
        assertThatThrownBy(() -> new Email("invalid-email"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid email format");
    }

    @Test
    @DisplayName("street이 blank이면 Address 생성 시 예외 발생")
    void address_street_blank이면_예외() {
        assertThatThrownBy(() -> new Address("", "101호", "12345"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Street cannot be blank");
    }
}
