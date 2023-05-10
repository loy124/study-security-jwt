package com.onyou.firstproject.member.service;

import com.onyou.firstproject.utils.JwtTokenUtil;
import com.onyou.firstproject.member.dto.MemberDto;
import com.onyou.firstproject.member.entity.Member;
import com.onyou.firstproject.member.entity.MemberRole;
import com.onyou.firstproject.member.entity.Role;
import com.onyou.firstproject.member.entity.RoleName;
import com.onyou.firstproject.member.repository.MemberRepository;
import com.onyou.firstproject.member.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Value("${jwt.token.secret}")
    private String key;

    private Long expireTimeMs = 1000 * 60 * 60l;

    @Autowired
    private  EntityManager em;

    private final RoleRepository roleRepository;


    /**
     * 회원가입
     * Role의 user를 넣어주는 작업이 필요하다.
     */
    @Transactional
    @Override
    public Long signUp(MemberDto.MemberSignUpRequestDto memberSignUpRequestDto) throws Exception {

        //이메일 검증 하기
        validateDuplicateMember(memberSignUpRequestDto.toEntity());

        Member member = memberRepository.save(memberSignUpRequestDto.toEntity());
        member.encodePassword(bCryptPasswordEncoder);


        //값 넣어주기 연간관계 편의 메서드를 넣어주자.
        Role role = roleRepository.findByRoleName(RoleName.USER);

        if(role == null){
            role = new Role(RoleName.USER);
            em.persist(role);
        }

        MemberRole memberRole = MemberRole.builder()
                .role(role)
                .member(member)
                .build();

        em.persist(memberRole);

        member.getMemberRoles().add(memberRole);


        return member.getId();
    }

    public String login(String email, String password){
        Member selectedMember = memberRepository.findByEmail(email);

        if(!bCryptPasswordEncoder.matches(selectedMember.getPassword(), password)){
            System.out.println("비밀번호가 일치하지 않습니다");
        }


        String token = JwtTokenUtil.createToken(selectedMember.getEmail(), key, expireTimeMs);

        return token;



    }



    private void validateDuplicateMember(Member member) {
        List<Member> findMembers = memberRepository.findListByEmail(member.getEmail());
        if (!findMembers.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }
}
