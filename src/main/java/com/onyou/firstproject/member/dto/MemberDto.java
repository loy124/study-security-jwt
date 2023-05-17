package com.onyou.firstproject.member.dto;

import com.onyou.firstproject.member.entity.Member;
import com.onyou.firstproject.member.entity.Role;
import lombok.*;

import javax.validation.constraints.NotBlank;

public class MemberDto {

    @Data
    public static class LoginRequestDto {
        private String email;
        private String password;
    }


    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class MemberSignUpRequestDto {

        @NotBlank
        private String email;

        @NotBlank
        private String username;

        @NotBlank
        private String password;

        private String provider;
        private String providerId;

        private Role role;

        public MemberSignUpRequestDto(String email, String username, String password) {
            this.email = email;
            this.username = username;
            this.password = password;
        }

        public MemberSignUpRequestDto(String email, String username,String provider, String providerId) {
            this.email = email;
            this.username = username;
            this.provider = provider;
            this.providerId = providerId;
        }

        @Builder
        public Member toEntity() {


            Member member = Member.builder()
                    .email(email)
                    .username(username)
                    .password(password)
                    .providerId(providerId)
                    .provider(provider)
                    .build();
            return member;
        }


    }


    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class MemberLoginResponseDto {
        private String token;

        public MemberLoginResponseDto(String token) {
            this.token = token;
        }
    }



}
