package com.example.orderservice.member.domain.repository;

import com.example.orderservice.member.domain.model.Email;
import com.example.orderservice.member.domain.model.Member;
import com.example.orderservice.member.domain.model.MemberId;

import java.util.Optional;

public interface MemberRepository {
    Member save(Member member);
    Optional<Member> findById(MemberId memberId);
    boolean existsByEmail(Email email);
}
