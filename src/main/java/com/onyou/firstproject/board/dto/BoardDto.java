package com.onyou.firstproject.board.dto;

import com.onyou.firstproject.board.entity.Board;
import lombok.*;

public class BoardDto {


    @Getter
    @RequiredArgsConstructor
    public static class CreateBoardDto {

        private String title;
        private String content;

        @Builder
        public Board toEntity(){
            return Board.builder()
                    .title(title)
                    .content(content)
                    .build();
        }

    }
}
