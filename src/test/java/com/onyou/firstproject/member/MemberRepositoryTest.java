package com.onyou.firstproject.member;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberRepositoryTest {

    @Autowired
    private EntityManager em;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Test
    public void testEntity(){

        //given
        String encodedPassword = bCryptPasswordEncoder.encode("test1234");
        Member member = Member.builder().
                username("test1234")
                .email("onyou.lee@mincoding.co.kr")
                .password(encodedPassword)
                .build();

        Member savedMember = memberRepository.save(member);
        //when
        Member member1 = memberRepository.findByEmail("onyou.lee@mincoding.co.kr");


        //then
        assertThat(member1.getUsername().equals(member.getUsername()));
        assertThat(member1.getEmail().equals(member.getEmail()));
        //비밀번호 암호화 검증
        assertTrue(bCryptPasswordEncoder.matches("test1234", member1.getPassword()));

    }

}