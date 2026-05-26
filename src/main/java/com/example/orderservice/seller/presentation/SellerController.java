package com.example.orderservice.seller.presentation;

import com.example.orderservice.seller.application.RegisterSellerCommand;
import com.example.orderservice.seller.application.SellerApplicationService;
import com.example.orderservice.seller.application.SellerResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sellers")
@RequiredArgsConstructor
public class SellerController {

    private final SellerApplicationService sellerApplicationService;

    @PostMapping
    public ResponseEntity<SellerApiResponse> register(@RequestBody @Valid RegisterSellerRequest request) {
        RegisterSellerCommand command = new RegisterSellerCommand(
                request.getBusinessName(),
                request.getPhone(),
                request.getEmail()
        );
        SellerResponse response = sellerApplicationService.register(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(toApiResponse(response));
    }

    @GetMapping("/{sellerId}")
    public ResponseEntity<SellerApiResponse> findById(@PathVariable Long sellerId) {
        SellerResponse response = sellerApplicationService.findById(sellerId);
        return ResponseEntity.ok(toApiResponse(response));
    }

    private SellerApiResponse toApiResponse(SellerResponse response) {
        return SellerApiResponse.builder()
                .sellerId(response.getSellerId())
                .businessName(response.getBusinessName())
                .phone(response.getPhone())
                .email(response.getEmail())
                .status(response.getStatus())
                .createdAt(response.getCreatedAt())
                .build();
    }
}
