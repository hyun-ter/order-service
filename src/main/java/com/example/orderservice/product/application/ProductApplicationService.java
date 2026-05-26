package com.example.orderservice.product.application;

import com.example.orderservice.common.exception.ProductNotFoundException;
import com.example.orderservice.common.vo.Money;
import com.example.orderservice.product.domain.model.Category;
import com.example.orderservice.product.domain.model.Product;
import com.example.orderservice.product.domain.model.ProductId;
import com.example.orderservice.product.domain.repository.ProductRepository;
import com.example.orderservice.seller.domain.model.SellerId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductApplicationService {

    private final ProductRepository productRepository;

    @Transactional
    public ProductResponse register(RegisterProductCommand command) {
        SellerId sellerId = new SellerId(command.getSellerId());
        Money price = Money.of(command.getPrice());
        Category category = new Category(command.getCategory());
        Product product = Product.register(sellerId, command.getName(), price, category);
        Product saved = productRepository.save(product);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public ProductResponse findById(Long productId) {
        ProductId id = new ProductId(productId);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(productId));
        return toResponse(product);
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> findBySellerId(Long sellerId) {
        SellerId id = new SellerId(sellerId);
        return productRepository.findBySellerId(id).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> findByCategory(String category) {
        Category cat = new Category(category);
        return productRepository.findByCategory(cat).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private ProductResponse toResponse(Product product) {
        return ProductResponse.builder()
                .productId(product.getProductId() != null ? product.getProductId().getId() : null)
                .sellerId(product.getSellerId().getId())
                .name(product.getName())
                .price(product.getPrice().getAmount())
                .category(product.getCategory().getName())
                .status(product.getStatus().name())
                .build();
    }
}
