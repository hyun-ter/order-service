package com.example.orderservice.member.presentation;

import com.example.orderservice.member.application.MemberApplicationService;
import com.example.orderservice.member.application.MemberResponse;
import com.example.orderservice.member.application.RegisterMemberCommand;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberApplicationService memberApplicationService;

    @PostMapping
    public ResponseEntity<MemberApiResponse> register(@RequestBody @Valid RegisterMemberRequest request) {
        RegisterMemberCommand command = new RegisterMemberCommand(
                request.getEmail(),
                request.getName(),
                request.getStreet(),
                request.getDetail(),
                request.getZipCode()
        );
        MemberResponse response = memberApplicationService.register(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(toApiResponse(response));
    }

    @GetMapping("/{memberId}")
    public ResponseEntity<MemberApiResponse> findById(@PathVariable Long memberId) {
        MemberResponse response = memberApplicationService.findById(memberId);
        return ResponseEntity.ok(toApiResponse(response));
    }

    private MemberApiResponse toApiResponse(MemberResponse response) {
        return MemberApiResponse.builder()
                .memberId(response.getMemberId())
                .email(response.getEmail())
                .name(response.getName())
                .street(response.getStreet())
                .detail(response.getDetail())
                .zipCode(response.getZipCode())
                .createdAt(response.getCreatedAt())
                .build();
    }
}
