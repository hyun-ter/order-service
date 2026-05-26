package com.example.orderservice.seller.domain.model;

import java.util.Objects;

public final class Contact {

    private final String phone;
    private final String email;

    public Contact(String phone, String email) {
        if (phone == null) {
            throw new IllegalArgumentException("Contact phone cannot be null");
        }
        if (email == null) {
            throw new IllegalArgumentException("Contact email cannot be null");
        }
        this.phone = phone;
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Contact contact = (Contact) o;
        return Objects.equals(phone, contact.phone) && Objects.equals(email, contact.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(phone, email);
    }
}
