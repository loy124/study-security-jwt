package com.onyou.firstproject.member;

import com.onyou.firstproject.board.Board;
import com.onyou.firstproject.common.BaseEntity;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString(exclude = {"boards"})
public class Member extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="member_id")
    private Long id;

    // 외부에서 가져오더라도 Entity 를 보고 제약조건을 파악하기 위해 사용
    @Column(unique = true)
    private String email;
    private String password;
    private String username;


    @OneToMany(mappedBy = "member")
    private List<Board> boards = new ArrayList<>();


    @Builder
    public Member(Long id, String email, String password, String username) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.username = username;
    }



}
