package com.onyou.firstproject.board;

import com.onyou.firstproject.board.entity.Board;
import com.onyou.firstproject.board.repository.BoardRepository;
import com.onyou.firstproject.member.entity.Member;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(false)
class BoardRepositoryTest {

    @Autowired
    private EntityManager em;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;


    @Test
    public void 관계형_검증(){
        //given
        String encodedPassword = bCryptPasswordEncoder.encode("test1234");
        Member member = Member.builder().
                username("test1234")
                .email("onyou.lee@mincoding.co.kr")
                .password(encodedPassword)
                .build();
        em.persist(member);

        Board board = Board.builder()
                .title("제목")
                .content("내용")
                .build();

        em.persist(board);

        board.setMember(member);

        //제목 내용, 그 외 조건은 동적쿼리로 빼보자

        //when
        List<Board> boards = boardRepository.findAll();
        Board board1 = boards.get(0);
        Member member1 = board1.getMember();
        System.out.println("member1 = " + member1);

        List<Board> boards1 = member1.getBoards();
        for (Board board2 : boards1) {
            System.out.println("board2 = " + board2);
        }

        //then
        //멤버가 같은지 검증

        assertThat(board1.getTitle().equals(board.getTitle()));
        assertThat(board1.getContent().equals(board.getContent()));
        assertThat(member1.getEmail().equals(member.getEmail()));

    }


}





