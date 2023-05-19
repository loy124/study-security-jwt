package com.onyou.firstproject.board.service;

import com.onyou.firstproject.board.dto.BoardDto;
import com.onyou.firstproject.board.entity.Board;
import com.onyou.firstproject.board.repository.BoardRepository;
import com.onyou.firstproject.config.auth.PrincipalDetails;
import com.onyou.firstproject.exception.Exception404;
import com.onyou.firstproject.member.entity.Member;
import com.onyou.firstproject.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.Optional;

import static com.onyou.firstproject.board.dto.BoardDto.*;

@Service
@Transactional
public class BoardService {

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private MemberRepository memberRepository;

    public void create(CreateBoardDto createBoardDto, Authentication authentication){

        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        String email = principal.getMember().getEmail();

        Member findMember = memberRepository.findByEmail(email).orElseThrow(()->new Exception404("해당 유저가 존재하지 않습니다"));

        Board board = createBoardDto.toEntity();

        Board savedBoard = boardRepository.save(board);
        savedBoard.setMember(findMember);



    }
}
