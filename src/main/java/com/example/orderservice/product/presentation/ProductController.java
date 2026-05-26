package com.example.orderservice.product.presentation;

import com.example.orderservice.product.application.ProductApplicationService;
import com.example.orderservice.product.application.ProductResponse;
import com.example.orderservice.product.application.RegisterProductCommand;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductApplicationService productApplicationService;

    @PostMapping
    public ResponseEntity<ProductApiResponse> register(@RequestBody @Valid RegisterProductRequest request) {
        RegisterProductCommand command = new RegisterProductCommand(
                request.getSellerId(),
                request.getName(),
                request.getPrice(),
                request.getCategory()
        );
        ProductResponse response = productApplicationService.register(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(toApiResponse(response));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductApiResponse> findById(@PathVariable Long productId) {
        ProductResponse response = productApplicationService.findById(productId);
        return ResponseEntity.ok(toApiResponse(response));
    }

    @GetMapping
    public ResponseEntity<List<ProductApiResponse>> findByQuery(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Long sellerId) {

        if (category == null && sellerId == null) {
            return ResponseEntity.badRequest().build();
        }

        List<ProductResponse> responses;
        if (category != null) {
            responses = productApplicationService.findByCategory(category);
        } else {
            responses = productApplicationService.findBySellerId(sellerId);
        }

        List<ProductApiResponse> apiResponses = responses.stream()
                .map(this::toApiResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(apiResponses);
    }

    private ProductApiResponse toApiResponse(ProductResponse response) {
        return ProductApiResponse.builder()
                .productId(response.getProductId())
                .sellerId(response.getSellerId())
                .name(response.getName())
                .price(response.getPrice())
                .category(response.getCategory())
                .status(response.getStatus())
                .build();
    }
}
