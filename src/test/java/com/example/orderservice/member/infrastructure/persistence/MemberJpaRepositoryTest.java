package com.example.orderservice.member.infrastructure.persistence;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class MemberJpaRepositoryTest {

    @Autowired
    MemberJpaRepository memberJpaRepository;

    @Test
    @DisplayName("이메일로 존재 여부 확인")
    void 이메일로_존재여부_확인() {
        MemberJpaEntity entity = MemberJpaEntity.builder()
                .email("test@test.com")
                .name("테스터")
                .street("서울")
                .detail("101호")
                .zipCode("12345")
                .createdAt(LocalDateTime.now())
                .build();
        memberJpaRepository.save(entity);

        assertThat(memberJpaRepository.existsByEmail("test@test.com")).isTrue();
        assertThat(memberJpaRepository.existsByEmail("other@test.com")).isFalse();
    }

    @Test
    @DisplayName("ID로 회원 조회")
    void ID로_회원조회() {
        MemberJpaEntity entity = MemberJpaEntity.builder()
                .email("test@test.com")
                .name("테스터")
                .street("서울")
                .detail("101호")
                .zipCode("12345")
                .createdAt(LocalDateTime.now())
                .build();
        MemberJpaEntity saved = memberJpaRepository.save(entity);

        Optional<MemberJpaEntity> found = memberJpaRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("test@test.com");
    }
}
