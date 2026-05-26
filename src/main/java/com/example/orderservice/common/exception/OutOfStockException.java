package com.example.orderservice.common.exception;

public class OutOfStockException extends RuntimeException {

    public OutOfStockException(Long productId, int requested, int available) {
        super("재고 부족: productId=" + productId + ", 요청=" + requested + ", 현재=" + available);
    }

    public OutOfStockException(String message) {
        super(message);
    }
}
