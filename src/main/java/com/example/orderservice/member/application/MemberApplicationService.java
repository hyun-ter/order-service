package com.example.orderservice.member.application;

import com.example.orderservice.common.exception.DuplicateEmailException;
import com.example.orderservice.common.exception.MemberNotFoundException;
import com.example.orderservice.member.domain.model.Address;
import com.example.orderservice.member.domain.model.Email;
import com.example.orderservice.member.domain.model.Member;
import com.example.orderservice.member.domain.model.MemberId;
import com.example.orderservice.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberApplicationService {

    private final MemberRepository memberRepository;

    @Transactional
    public MemberResponse register(RegisterMemberCommand command) {
        Email email = new Email(command.getEmail());

        if (memberRepository.existsByEmail(email)) {
            throw new DuplicateEmailException(command.getEmail());
        }

        Address address = new Address(command.getStreet(), command.getDetail(), command.getZipCode());
        Member member = Member.register(email, command.getName(), address);
        Member saved = memberRepository.save(member);

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public MemberResponse findById(Long memberId) {
        MemberId id = new MemberId(memberId);
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new MemberNotFoundException(memberId));
        return toResponse(member);
    }

    private MemberResponse toResponse(Member member) {
        return MemberResponse.builder()
                .memberId(member.getMemberId() != null ? member.getMemberId().getId() : null)
                .email(member.getEmail().getValue())
                .name(member.getName())
                .street(member.getAddress().getStreet())
                .detail(member.getAddress().getDetail())
                .zipCode(member.getAddress().getZipCode())
                .createdAt(member.getCreatedAt())
                .build();
    }
}
