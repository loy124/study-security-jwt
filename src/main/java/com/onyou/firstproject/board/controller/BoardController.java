package com.onyou.firstproject.board.controller;

import com.onyou.firstproject.board.dto.BoardDto;
import com.onyou.firstproject.board.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static com.onyou.firstproject.board.dto.BoardDto.*;

@RestController
@RequestMapping("/api/board")
public class BoardController {
    @Autowired
    private BoardService boardService;


    @PostMapping
    public ResponseEntity<String> create(@RequestBody @Valid CreateBoardDto createBoardDto, Authentication authentication){
        boardService.create(createBoardDto, authentication);


        return ResponseEntity.ok().body(authentication.getName() + "님의 리뷰 등록이 완료되었습니다");
    }


}
