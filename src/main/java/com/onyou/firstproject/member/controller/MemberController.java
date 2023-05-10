package com.onyou.firstproject.member.controller;


import com.onyou.firstproject.member.dto.MemberDto;
import com.onyou.firstproject.member.service.MemberServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/member")
public class MemberController {

    @Autowired
    private MemberServiceImpl memberService;

    //DTO를 인자로 받아서 DTO를 통해 값을 내보내는 이유 : API 스펙을 고치지 않기 위함
    // @NotNull 같은 옵션을 Entity에 직접주게되면 이를 수정할 경우 API 스펙들이 변하게된다
    // 따라서 API 요청 스펙에 맞춰서 각자 DTO를 파라미터로 받고 내보내준다.
    @PostMapping
    public Long signUp(@RequestBody @Valid MemberDto.MemberSignUpRequestDto memberSignUpRequestDto) throws Exception{
        Long signUp = memberService.signUp(memberSignUpRequestDto);

        return signUp;

    }

    @PostMapping("login")
    public String login(@RequestBody @Valid MemberDto.LoginRequestDto loginRequestDto) throws Exception{
        String signUp = memberService.login(loginRequestDto.getEmail(), loginRequestDto.getPassword());

        return signUp;

    }


}
