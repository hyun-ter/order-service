package com.example.orderservice.common.exception;

public class InventoryNotFoundException extends RuntimeException {

    public InventoryNotFoundException(String message) {
        super(message);
    }

    public InventoryNotFoundException(Long productId) {
        super("재고 정보를 찾을 수 없습니다: productId=" + productId);
    }
}
