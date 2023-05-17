package com.onyou.firstproject.board.service;

import com.onyou.firstproject.board.dto.BoardDto;
import com.onyou.firstproject.board.entity.Board;
import com.onyou.firstproject.board.repository.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import static com.onyou.firstproject.board.dto.BoardDto.*;

@Service
public class BoardService {

    @Autowired
    private BoardRepository boardRepository;

    public void create(CreateBoardDto createBoardDto, Authentication authentication){
        Board board = createBoardDto.toEntity();




        boardRepository.save(board);

    }
}
