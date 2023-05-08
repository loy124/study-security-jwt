package com.onyou.firstproject.member.repository;


import com.onyou.firstproject.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    //회원가입 로직

    //Email로 찾기

    Member findByEmail(@Param("email") String email);

    @Query("select m from Member m left join fetch m.memberRoles")
    Member findJoinByEmail(@Param("email") String email);

    List<Member> findListByEmail(@Param("email") String email);

}
