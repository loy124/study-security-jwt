package com.onyou.firstproject.member.service;

import com.onyou.firstproject.config.auth.PrincipalDetails;
import com.onyou.firstproject.exception.Exception403;
import com.onyou.firstproject.exception.Exception404;
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
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;

import static com.onyou.firstproject.utils.JwtTokenUtil.*;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Value("${jwt.token.secret}")
    private String key;
//    private Long expireTimeMs = 1000 * 60 * 60l; // 1시간
//    private Long refreshExpireTimeMs = 1000 * 60 * 60l * 24 * 14; //24시간 * 14

    @Autowired
    private  EntityManager em;

    private final RoleRepository roleRepository;


    /**
     * 회원가입
     * Role의 user를 넣어주는 작업이 필요하다.
     */
    @Transactional
    public Long signUp(MemberDto.MemberSignUpRequestDto memberSignUpRequestDto) throws Exception {

        //이메일 검증 하기
        validateDuplicateMember(memberSignUpRequestDto.toEntity());

        Member member = memberRepository.save(memberSignUpRequestDto.toEntity());
        member.encodePassword(bCryptPasswordEncoder);


        //값 넣어주기 연간관계 편의 메서드를 넣어주자.
        Role role = getOrSaveRole();


        addMemberRole(member, role);


        return member.getId();
    }

    /*
     * Oauth2의 회원가입
     * */

    @Transactional
    public Long signUp(Member member) throws Exception {

        //이메일 검증 하기
        validateDuplicateMember(member);


        Member saveMember = memberRepository.save(member);


        //값 넣어주기 연간관계 편의 메서드를 넣어주자.
        Role role = getOrSaveRole();

        addMemberRole(saveMember, role);

        return member.getId();
    }


    private void addMemberRole(Member member, Role role) {
        MemberRole memberRole = MemberRole.builder()
                .role(role)
                .member(member)
                .build();

        em.persist(memberRole);

        member.getMemberRoles().add(memberRole);
    }

    private Role getOrSaveRole() {
        Role role = roleRepository.findByRoleName(RoleName.USER);

        if(role == null){
            role = new Role(RoleName.USER);
            em.persist(role);
        }
        return role;
    }



    public String login(String email, String password){

        if(password == null){
            return null;
        }

        Member selectedMember = memberRepository.findByEmail(email).orElseThrow(() -> new Exception404("해당 유저가 없습니다."));

        //TODO Execption 처리
        if(!bCryptPasswordEncoder.matches(password, selectedMember.getPassword())){
            System.out.println("비밀번호가 일치하지 않습니다");

            return null;
        }


        return createToken(selectedMember.getEmail());

    }

    public String parseEmailFromTokenAndCreateToken(String refreshToken){
        String email = getEmail(refreshToken, key);
        String token = createToken(email);

        return token;
    }

    public String createToken(String email) {
        String token = JwtTokenUtil.createToken(email, key, accessExpireTimeMs);

        return token;
    }


    private void validateDuplicateMember(Member member) {
        Optional<Member> findMember = memberRepository
                .findByEmail(member.getEmail());

        if(!findMember.isEmpty()){
            throw new Exception403("이미 존재하는 회원입니다");
        }

    }

    public void setRefreshToken(String email, HttpServletResponse response) {
        String refreshToken = JwtTokenUtil.createToken(email, key, refreshExpireTimeMs);
        Cookie cookie = new Cookie("refresh-token", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    public void removeRefreshToken(HttpServletResponse response) {

        Cookie cookie = new Cookie("refresh-token", null);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
