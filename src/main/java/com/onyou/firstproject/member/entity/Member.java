package com.onyou.firstproject.member.entity;

import com.onyou.firstproject.board.entity.Board;
import com.onyou.firstproject.common.BaseEntity;
import lombok.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.*;
import java.util.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString(exclude = {"boards", "memberRoles"})
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

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    private List<Board> boards = new ArrayList<>();


    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    private List<MemberRole> memberRoles = new ArrayList<>();



    @Builder
    public Member(String email, String password, String username) {
        this.email = email;
        this.password = password;
        this.username = username;

    }


    //편의 메서드

    public void encodePassword(BCryptPasswordEncoder bCryptPasswordEncoder){
        this.password = bCryptPasswordEncoder.encode(password);
    }

    //==연관관계 메서드==//
    //관리자의 입장에서 memberId를 받고, Role의 id를 받아서 이를 기반으로 memberRole을 생성해야한다.
    // 먼저 memberRole을 생성하고 이걸 Member에서 등록해줘야한다.

    //유저가 멤버를 만드는데 MemberRole을 만들어서 같이 보내준다.






}
