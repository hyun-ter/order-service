package com.example.orderservice.inventory.presentation;

import com.example.orderservice.inventory.application.InventoryApplicationService;
import com.example.orderservice.inventory.application.InventoryResponse;
import com.example.orderservice.inventory.application.RegisterInventoryCommand;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventories")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryApplicationService inventoryApplicationService;

    @PostMapping
    public ResponseEntity<InventoryApiResponse> register(@RequestBody @Valid RegisterInventoryRequest request) {
        RegisterInventoryCommand command = new RegisterInventoryCommand(
                request.getProductId(),
                request.getQuantity()
        );
        InventoryResponse response = inventoryApplicationService.register(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(toApiResponse(response));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<InventoryApiResponse> findByProductId(@PathVariable Long productId) {
        InventoryResponse response = inventoryApplicationService.findByProductId(productId);
        return ResponseEntity.ok(toApiResponse(response));
    }

    @PatchMapping("/{productId}/increase")
    public ResponseEntity<InventoryApiResponse> increaseStock(
            @PathVariable Long productId,
            @RequestBody @Valid IncreaseInventoryRequest request) {
        InventoryResponse response = inventoryApplicationService.increaseStock(productId, request.getAmount());
        return ResponseEntity.ok(toApiResponse(response));
    }

    private InventoryApiResponse toApiResponse(InventoryResponse response) {
        return InventoryApiResponse.builder()
                .inventoryId(response.getInventoryId())
                .productId(response.getProductId())
                .quantity(response.getQuantity())
                .build();
    }
}
