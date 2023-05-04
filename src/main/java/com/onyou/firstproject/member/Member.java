package com.onyou.firstproject.member;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Member {
    @Id @GeneratedValue
    @Column(name="member_id")
    private Long id;

    // 외부에서 가져오더라도 Entity 를 보고 제약조건을 파악하기 위해 사용
    @Column(unique = true)
    private String email;
    private String password;
    private String username;


}
