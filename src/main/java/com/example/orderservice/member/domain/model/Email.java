package com.example.orderservice.member.domain.model;

import java.util.Objects;

/**
 * 이메일 주소를 나타내는 Value Object.
 *
 * <p>생성 시점에 이메일 형식을 검증하므로, Email 인스턴스가 존재하면 항상 유효한 이메일이다.
 * Presentation 레이어의 {@code @Email} 검증과 별개로, 도메인 규칙을 자체적으로 보장한다.
 */
public final class Email {

    private static final java.util.regex.Pattern EMAIL_PATTERN =
        java.util.regex.Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private final String value;

    public Email(String value) {
        if (value == null || !EMAIL_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("Invalid email format: " + value);
        }
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Email email = (Email) o;
        return Objects.equals(value, email.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
