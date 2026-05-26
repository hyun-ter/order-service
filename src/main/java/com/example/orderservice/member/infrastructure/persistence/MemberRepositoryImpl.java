package com.example.orderservice.member.infrastructure.persistence;

import com.example.orderservice.member.domain.model.Email;
import com.example.orderservice.member.domain.model.Member;
import com.example.orderservice.member.domain.model.MemberId;
import com.example.orderservice.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepository {

    private final MemberJpaRepository memberJpaRepository;

    @Override
    public Member save(Member member) {
        MemberJpaEntity entity = MemberJpaEntity.fromMember(member);
        MemberJpaEntity saved = memberJpaRepository.save(entity);
        return saved.toMember();
    }

    @Override
    public Optional<Member> findById(MemberId memberId) {
        return memberJpaRepository.findById(memberId.getId())
                .map(MemberJpaEntity::toMember);
    }

    @Override
    public boolean existsByEmail(Email email) {
        return memberJpaRepository.existsByEmail(email.getValue());
    }
}
