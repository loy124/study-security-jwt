package com.onyou.firstproject.member.service;

import com.onyou.firstproject.member.dto.MemberDto;
import com.onyou.firstproject.member.entity.Member;
import com.onyou.firstproject.member.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static com.onyou.firstproject.member.dto.MemberDto.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MemberServiceImplTest {

    @Autowired
    private MemberServiceImpl memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;


//    회원가입
    @Test
    public void 일반_유저_회원가입() throws Exception {
//        String email = "onyou.lee@mincoding.co.kr";
        String email = "onyou.lee@mincoding.co.kr";
        String password = "test1234";
        String username = "이온유";
        MemberSignUpRequestDto memberSignUpRequestDto = new MemberSignUpRequestDto(email, username, password);


        Long signUpId = memberService.signUp(memberSignUpRequestDto);
        //when


        Optional<Member> findMember = memberRepository.findById(signUpId);


        //then
        assertThat(findMember.get().getEmail().equals(email));

//        assertThat(findMember.get().getRole().equals("USER"));
//        assertThat(findMember.get().getRoles().contains("USER"));

        assertTrue(bCryptPasswordEncoder.matches(password, findMember.get().getPassword()));

    }
//    중복회원예외

    @Test
    public void 일반_유저_회원가입_이메일중복() throws Exception {
//        String email = "onyou.lee@mincoding.co.kr";
        String email = "onyou.lee@mincoding.co.kr";
        String password = "test1234";
        String username = "이온유";
        MemberSignUpRequestDto memberSignUpRequestDto = new MemberSignUpRequestDto(email, username, password);
        MemberSignUpRequestDto memberSignUpRequestDto2 = new MemberSignUpRequestDto(email, username, password);

        //when

        Long signUpId = memberService.signUp(memberSignUpRequestDto);

        assertThrows(DataIntegrityViolationException.class, () -> {
            memberService.signUp(memberSignUpRequestDto2);
        });



//        Optional<Member> findMember = memberRepository.findById(signUpId);






    }



}