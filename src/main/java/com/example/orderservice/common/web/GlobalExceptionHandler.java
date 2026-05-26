package com.example.orderservice.common.web;

import com.example.orderservice.common.exception.DuplicateEmailException;
import com.example.orderservice.common.exception.InvalidOrderStatusException;
import com.example.orderservice.common.exception.InventoryNotFoundException;
import com.example.orderservice.common.exception.MemberNotFoundException;
import com.example.orderservice.common.exception.OrderNotFoundException;
import com.example.orderservice.common.exception.OutOfStockException;
import com.example.orderservice.common.exception.ProductNotFoundException;
import com.example.orderservice.common.exception.SellerNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * 전역 예외 처리 핸들러.
 *
 * <p>모든 Controller에서 발생하는 예외를 일관된 JSON 응답으로 변환한다.
 * 각 도메인 예외에 적절한 HTTP 상태코드를 매핑한다.
 *
 * <p>응답 형식: {@code {"code": "NOT_FOUND", "message": "..."}}
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** 404 — 조회 대상 리소스가 존재하지 않는 경우 */
    @ExceptionHandler({MemberNotFoundException.class, SellerNotFoundException.class,
                       OrderNotFoundException.class, ProductNotFoundException.class,
                       InventoryNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleNotFoundException(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse("NOT_FOUND", e.getMessage()));
    }

    /** 409 — 이메일 중복 또는 재고 부족으로 요청을 처리할 수 없는 경우 */
    @ExceptionHandler({DuplicateEmailException.class, OutOfStockException.class})
    public ResponseEntity<ErrorResponse> handleConflictException(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(new ErrorResponse("CONFLICT", e.getMessage()));
    }

    /** 400 — 허용되지 않는 주문 상태 전이 시도 */
    @ExceptionHandler(InvalidOrderStatusException.class)
    public ResponseEntity<ErrorResponse> handleBadRequestException(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse("BAD_REQUEST", e.getMessage()));
    }

    /** 400 — @Valid 검증 실패 시 필드별 오류 메시지를 포함하여 반환 */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.joining(", "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse("VALIDATION_ERROR", message));
    }

    /** 400 — Value Object 생성 시 도메인 규칙 위반 (이메일 형식, 음수 금액 등) */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse("INVALID_INPUT", e.getMessage()));
    }

    /** 500 — 예상치 못한 서버 내부 오류. 내부 메시지는 노출하지 않는다. */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ErrorResponse("INTERNAL_ERROR", "서버 오류가 발생했습니다."));
    }

    /** API 에러 응답 공통 포맷 */
    public static class ErrorResponse {
        private final String code;
        private final String message;

        public ErrorResponse(String code, String message) {
            this.code = code;
            this.message = message;
        }

        public String getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }
    }
}
