package com.onyou.firstproject.config.oauth;

import com.onyou.firstproject.config.auth.PrincipalDetails;
import com.onyou.firstproject.config.oauth.provider.GoogleUserInfo;
import com.onyou.firstproject.config.oauth.provider.KakaoUserInfo;
import com.onyou.firstproject.config.oauth.provider.NaverUserInfo;
import com.onyou.firstproject.config.oauth.provider.OAuth2UserInfo;
import com.onyou.firstproject.member.dto.MemberDto;
import com.onyou.firstproject.member.entity.Member;
import com.onyou.firstproject.member.entity.MemberRole;
import com.onyou.firstproject.member.entity.Role;
import com.onyou.firstproject.member.entity.RoleName;
import com.onyou.firstproject.member.repository.MemberRepository;
import com.onyou.firstproject.member.repository.RoleRepository;
import com.onyou.firstproject.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.nio.file.attribute.UserPrincipal;
import java.util.List;
import java.util.Map;

import static com.onyou.firstproject.member.dto.MemberDto.*;

@Service
@RequiredArgsConstructor
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {


    private final EntityManager em;

    private final MemberRepository memberRepository;
    private final RoleRepository roleRepository;

    @Transactional
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        System.out.println("PrincipalOauth2UserService.loadUser");
        //type을 알아야한다.
        OAuth2User oAuth2User = super.loadUser(userRequest);


        ClientRegistration clientRegistration = userRequest.getClientRegistration();

        OAuth2UserInfo oAuth2UserInfo = null;


        //google
        if(clientRegistration.getRegistrationId().equals("google")){
            //여기서 받아온 정보를
            oAuth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());

        }else if(clientRegistration.getRegistrationId().equals("naver")){
            //response 안에 있다.
            oAuth2UserInfo = new NaverUserInfo((Map<String, Object>) oAuth2User.getAttributes().get("response"));

        }else if(clientRegistration.getRegistrationId().equals("kakao")){
            oAuth2UserInfo = new KakaoUserInfo(oAuth2User.getAttributes());
        }

        Member member = Member.builder()
                .email(oAuth2UserInfo.getEmail())
                .username(oAuth2UserInfo.getName())
                .provider(oAuth2UserInfo.getProvider())
                .providerId(oAuth2UserInfo.getProviderId())
                .build();

        PrincipalDetails principalDetails = null;

        //이메일 검증 하기


        Member findMember = memberRepository.findJoinByEmail(member.getEmail());

        //아이디가 존재하는 경우
        // 해당 멤버를 리턴처리 하기
        if(findMember != null){
            principalDetails = new PrincipalDetails(findMember);
            return principalDetails;
        };



        //회원가입 처리
        Member savedMember = memberRepository.save(member);

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

        principalDetails = new PrincipalDetails(member);

        return principalDetails;
    }

    private boolean validateDuplicateMember(Member member) {
        List<Member> findMembers = memberRepository.findListByEmail(member.getEmail());
        if (!findMembers.isEmpty()) {
            return false;
        }

        return true;
    }
}
