package com.example.orderservice.product.infrastructure.persistence;

import com.example.orderservice.common.vo.Money;
import com.example.orderservice.product.domain.model.Category;
import com.example.orderservice.product.domain.model.Product;
import com.example.orderservice.product.domain.model.ProductId;
import com.example.orderservice.product.domain.model.ProductStatus;
import com.example.orderservice.seller.domain.model.SellerId;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class ProductJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long sellerId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private String category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus status;

    public Product toProduct() {
        return Product.restore(
                new ProductId(id),
                new SellerId(sellerId),
                name,
                Money.of(price),
                new Category(category),
                status
        );
    }

    public static ProductJpaEntity fromProduct(Product product) {
        return ProductJpaEntity.builder()
                .id(product.getProductId() != null ? product.getProductId().getId() : null)
                .sellerId(product.getSellerId().getId())
                .name(product.getName())
                .price(product.getPrice().getAmount())
                .category(product.getCategory().getName())
                .status(product.getStatus())
                .build();
    }
}
