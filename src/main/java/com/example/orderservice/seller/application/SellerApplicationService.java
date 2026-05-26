package com.example.orderservice.seller.application;

import com.example.orderservice.common.exception.SellerNotFoundException;
import com.example.orderservice.seller.domain.model.BusinessName;
import com.example.orderservice.seller.domain.model.Contact;
import com.example.orderservice.seller.domain.model.Seller;
import com.example.orderservice.seller.domain.model.SellerId;
import com.example.orderservice.seller.domain.repository.SellerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SellerApplicationService {

    private final SellerRepository sellerRepository;

    @Transactional
    public SellerResponse register(RegisterSellerCommand command) {
        BusinessName businessName = new BusinessName(command.getBusinessName());
        Contact contact = new Contact(command.getPhone(), command.getEmail());
        Seller seller = Seller.register(businessName, contact);
        Seller saved = sellerRepository.save(seller);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public SellerResponse findById(Long sellerId) {
        SellerId id = new SellerId(sellerId);
        Seller seller = sellerRepository.findById(id)
                .orElseThrow(() -> new SellerNotFoundException(sellerId));
        return toResponse(seller);
    }

    private SellerResponse toResponse(Seller seller) {
        return SellerResponse.builder()
                .sellerId(seller.getSellerId() != null ? seller.getSellerId().getId() : null)
                .businessName(seller.getBusinessName().getValue())
                .phone(seller.getContact().getPhone())
                .email(seller.getContact().getEmail())
                .status(seller.getStatus().name())
                .createdAt(seller.getCreatedAt())
                .build();
    }
}
