package com.onyou.firstproject.member;

import com.onyou.firstproject.exception.Exception403;
import com.onyou.firstproject.exception.Exception404;
import com.onyou.firstproject.member.entity.Member;
import com.onyou.firstproject.member.entity.MemberRole;
import com.onyou.firstproject.member.entity.Role;
import com.onyou.firstproject.member.entity.RoleName;
import com.onyou.firstproject.member.repository.MemberRepository;
import com.onyou.firstproject.member.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.config.annotation.web.configurers.UrlAuthorizationConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.List;

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
    private RoleRepository roleRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private Member member;

    @BeforeEach
    public void 초반_값(){
        String encodedPassword = bCryptPasswordEncoder.encode("test1234");
        member = Member.builder().
                username("test1234")
                .email("onyou.lee@mincoding.co.kr")
                .password(encodedPassword)
                .build();

        Member savedMember = memberRepository.save(member);
    }

    @Test
    public void testEntity(){

        //given
//        String encodedPassword = bCryptPasswordEncoder.encode("test1234");
//        Member member = Member.builder().
//                username("test1234")
//                .email("onyou.lee@mincoding.co.kr")
//                .password(encodedPassword)
//                .build();

        Member savedMember = memberRepository.save(member);
        //when
        Member member1 = memberRepository.findByEmail("onyou.lee@mincoding.co.kr").orElseThrow(() -> new Exception404("해당 이메일을 가진 유저가 없습니다"));


        //then
        assertThat(member1.getUsername().equals(member.getUsername()));
        assertThat(member1.getEmail().equals(member.getEmail()));
        //비밀번호 암호화 검증
        assertTrue(bCryptPasswordEncoder.matches("test1234", member1.getPassword()));

    }

    /**
     * 여기서 회원가입
     * 유저 주입
     * 검증
     */

    @Test
    public void 회원가입(){

        //given
        Role role = new Role(RoleName.USER);
        Role role2 = new Role(RoleName.MANAGER);

        em.persist(role);
        em.persist(role2);

        //여기서 이제 member에 role을 추가해주는 로직이 필요하다.
        MemberRole memberRole = MemberRole.builder()
                .member(member)
                .role(role)
                .build();



        MemberRole memberRole2 = MemberRole.builder()
                .member(member)
                .role(role2)
                .build();

        em.persist(memberRole);
        em.persist(memberRole2);

        member.getMemberRoles().add(memberRole);
        member.getMemberRoles().add(memberRole2);
        //when

        em.flush();
        em.clear();

//        Member findMember = memberRepository.findByEmail("onyou.lee@mincoding.co.kr");
        Member findMember = memberRepository.findJoinByEmail("onyou.lee@mincoding.co.kr").orElseThrow(() -> new Exception403("유효하지 않습니다"));

        //then

        assertThat(findMember.getUsername().equals(member.getUsername()));
        assertThat(findMember.getEmail().equals(member.getEmail()));


        List<MemberRole> memberRoles = findMember.getMemberRoles();

        for (MemberRole memberRole1 : memberRoles) {
            System.out.println("memberRole1 = " + memberRole1);
            System.out.println("memberRole1 = " + memberRole1.getMember());
            System.out.println("memberRole1 = " + memberRole1.getRole());
        }




    }


}





