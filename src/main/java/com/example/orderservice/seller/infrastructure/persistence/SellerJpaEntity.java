package com.example.orderservice.seller.infrastructure.persistence;

import com.example.orderservice.seller.domain.model.BusinessName;
import com.example.orderservice.seller.domain.model.Contact;
import com.example.orderservice.seller.domain.model.Seller;
import com.example.orderservice.seller.domain.model.SellerId;
import com.example.orderservice.seller.domain.model.SellerStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "sellers")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class SellerJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String businessName;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String status;

    private LocalDateTime createdAt;

    public Seller toSeller() {
        return Seller.restore(
                new SellerId(id),
                new BusinessName(businessName),
                new Contact(phone, email),
                SellerStatus.valueOf(status),
                createdAt
        );
    }

    public static SellerJpaEntity fromSeller(Seller seller) {
        return SellerJpaEntity.builder()
                .id(seller.getSellerId() != null ? seller.getSellerId().getId() : null)
                .businessName(seller.getBusinessName().getValue())
                .phone(seller.getContact().getPhone())
                .email(seller.getContact().getEmail())
                .status(seller.getStatus().name())
                .createdAt(seller.getCreatedAt())
                .build();
    }
}
