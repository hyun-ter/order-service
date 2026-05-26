package com.example.orderservice.order.domain.service;

import com.example.orderservice.product.domain.model.Product;
import com.example.orderservice.product.domain.model.ProductStatus;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 주문 도메인 서비스.
 *
 * <p>단일 Aggregate에 속하지 않는 도메인 로직을 담는다.
 * 여기서는 주문 가능 여부를 검증하는데, 여러 Product Aggregate를 순회해야 하므로
 * Order Aggregate Root 내부에 넣기 부적절하다.
 *
 * <p>도메인 규칙: 주문 시 모든 상품은 ON_SALE 상태여야 한다.
 */
@Component
public class OrderDomainService {

    /**
     * 주문하려는 상품 목록이 모두 주문 가능한 상태인지 검증한다.
     *
     * @throws IllegalStateException ON_SALE이 아닌 상품이 포함된 경우
     */
    public void validateOrderable(List<Product> products) {
        for (Product product : products) {
            if (product.getStatus() != ProductStatus.ON_SALE) {
                throw new IllegalStateException("주문 불가 상품: " + product.getProductId().getId());
            }
        }
    }
}
