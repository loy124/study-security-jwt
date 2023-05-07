package com.onyou.firstproject.member.entity;

import com.onyou.firstproject.board.Board;
import com.onyou.firstproject.common.BaseEntity;
import lombok.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.*;
import java.util.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString(exclude = {"boards", "roles"})
public class Member extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="member_id")
    private Long id;

    // 외부에서 가져오더라도 Entity 를 보고 제약조건을 파악하기 위해 사용
    @Column(unique = true)
    private String email;
    private String password;
    private String username;


/*
* One to many
* */

    @OneToMany(mappedBy = "member")
    private List<Board> boards = new ArrayList<>();


    @OneToMany(mappedBy = "member")
    private List<MemberRole> memberRoles = new ArrayList<>();



    @Builder
    public Member(String email, String password, String username) {
        this.email = email;
        this.password = password;
        this.username = username;

    }








    public void encodePassword(BCryptPasswordEncoder bCryptPasswordEncoder){
        this.password = bCryptPasswordEncoder.encode(password);
    }


}
