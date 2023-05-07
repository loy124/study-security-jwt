package com.onyou.firstproject.member.dto;


import com.onyou.firstproject.member.entity.Member;
import com.onyou.firstproject.member.entity.Role;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberSignUpRequestDto {

    @NotBlank
    private  String email;

    @NotBlank
    private  String username;

    @NotBlank
    private  String password;

    private  Role role;

    public MemberSignUpRequestDto(String email, String username, String password) {
        this.email = email;
        this.username = username;
        this.password = password;
    }

    @Builder
    public Member toEntity(){


        Member member = Member.builder()
                .email(email)
                .username(username)
                .password(password)
                .build();

//        member.addRoles(Role.USER);

        return member;
    }


}
