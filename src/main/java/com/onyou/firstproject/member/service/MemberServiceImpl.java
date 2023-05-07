package com.onyou.firstproject.member.service;

import com.onyou.firstproject.member.dto.MemberSignUpRequestDto;
import com.onyou.firstproject.member.entity.Member;
import com.onyou.firstproject.member.repository.MemberRepository;
import com.onyou.firstproject.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;


    /**
     * 회원가입
     */
    @Transactional
    @Override
    public Long signUp(MemberSignUpRequestDto memberSignUpRequestDto) throws Exception {

        //이메일 검증 하기
        validateDuplicateMember(memberSignUpRequestDto.toEntity());

        Member member = memberRepository.save(memberSignUpRequestDto.toEntity());
        member.encodePassword(bCryptPasswordEncoder);

        return member.getId();
    }

    private void validateDuplicateMember(Member member) {
        List<Member> findMembers = memberRepository.findListByEmail(member.getEmail());
        if (!findMembers.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }
}
