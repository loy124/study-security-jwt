package com.onyou.firstproject.member.controller;


import com.onyou.firstproject.member.service.MemberService;
import com.onyou.firstproject.utils.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import static com.onyou.firstproject.member.dto.MemberDto.*;

@RestController
@RequestMapping("/api/member")
public class MemberController {

    @Autowired
    private MemberService memberService;

    /**
     *
     * @param memberSignUpRequestDto
     * @return Long
     * @throws Exception
     */
    @PostMapping
    public Long signUp(@RequestBody @Valid MemberSignUpRequestDto memberSignUpRequestDto) throws Exception{
        Long signUp = memberService.signUp(memberSignUpRequestDto);


        return signUp;

    }

    /**
     * 로그인 및 JWT Access Token 발급
     * @param loginRequestDto
     * @param response
     * @return
     * @throws Exception
     */

    @PostMapping("login")
    public ResponseEntity<MemberLoginResponseDto> login(@RequestBody @Valid LoginRequestDto loginRequestDto, HttpServletResponse response) throws Exception{
        String token = memberService.login(loginRequestDto.getEmail(), loginRequestDto.getPassword());

        if(token == null){
            memberService.removeRefreshToken(response);

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        memberService.setRefreshToken(loginRequestDto.getEmail(),response);

        MemberLoginResponseDto memberLoginResponseDto = new MemberLoginResponseDto(token);


        return new ResponseEntity<>(memberLoginResponseDto, HttpStatus.OK);

    }


    /**
     *
     * refresh token 유효성 검사
     * 유효한 경우 refresh token 유효 기간이  5일 이내로 남은 경우 리프레쉬 토큰도 갱신
     * accessToken 발급 후 리턴-> 프론트에서는 Authorization header 에 저장
     *
     */

    @PostMapping("silent-refresh")
    public ResponseEntity<?> silentRefresh(@CookieValue("refresh-token") String refreshToken){
        // System.out.println("refreshToken = " + refreshToken);
        // token이 null인지
        // token안에 값이 null이 아닌지

        //처음에 들어와서 silent-refresh의 경우 refreshToken에대한 유효성 검사를 한다 -> SilentFilter를 적용하고
        // 나머지는 기본 JWT 필터를 받고 있다.

        //특정 URL로 오는경우 해당 유효성 검사를 진행한다.


        if(refreshToken == null){
            return null;
        }

        String accessToken = memberService.parseEmailFromTokenAndCreateToken(refreshToken);


        //accessToken 생성해서 리턴하기
        MemberLoginResponseDto memberLoginResponseDto = new MemberLoginResponseDto(accessToken);
        return new ResponseEntity<>(memberLoginResponseDto, HttpStatus.OK);
    }





}
