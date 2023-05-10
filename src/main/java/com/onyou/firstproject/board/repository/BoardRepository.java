package com.onyou.firstproject.board.repository;

import com.onyou.firstproject.board.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long> {


}
