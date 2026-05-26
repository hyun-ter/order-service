package com.example.orderservice.member.domain.model;

import java.util.Objects;

public final class Address {

    private final String street;
    private final String detail;
    private final String zipCode;

    public Address(String street, String detail, String zipCode) {
        if (street == null || street.isBlank()) throw new IllegalArgumentException("Street cannot be blank");
        if (zipCode == null || zipCode.isBlank()) throw new IllegalArgumentException("ZipCode cannot be blank");
        this.street = street;
        this.detail = detail;
        this.zipCode = zipCode;
    }

    public String getStreet() {
        return street;
    }

    public String getDetail() {
        return detail;
    }

    public String getZipCode() {
        return zipCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return Objects.equals(street, address.street)
                && Objects.equals(detail, address.detail)
                && Objects.equals(zipCode, address.zipCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(street, detail, zipCode);
    }
}
