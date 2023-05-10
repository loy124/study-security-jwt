package com.onyou.firstproject.member.service;

import com.onyou.firstproject.member.dto.MemberDto;

public interface MemberService {

    //회원가입
    public Long signUp(MemberDto.MemberSignUpRequestDto memberSignUpRequestDto) throws Exception;


}
