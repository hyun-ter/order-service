package com.example.orderservice.order.domain.service;

import com.example.orderservice.product.domain.model.Product;
import com.example.orderservice.product.domain.model.ProductStatus;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderDomainService {

    public void validateOrderable(List<Product> products) {
        for (Product product : products) {
            if (product.getStatus() != ProductStatus.ON_SALE) {
                throw new IllegalStateException("주문 불가 상품: " + product.getProductId().getId());
            }
        }
    }
}
