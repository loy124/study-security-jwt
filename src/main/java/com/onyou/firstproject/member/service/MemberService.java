package com.onyou.firstproject.member.service;

import com.onyou.firstproject.member.dto.MemberSignUpRequestDto;

public interface MemberService {

    //회원가입
    public Long signUp(MemberSignUpRequestDto memberSignUpRequestDto) throws Exception;
}
