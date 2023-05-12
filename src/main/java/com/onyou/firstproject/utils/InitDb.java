package com.onyou.firstproject.utils;

import com.onyou.firstproject.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;

@Component
@RequiredArgsConstructor
public class InitDb {

    private final InitService initService;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @PostConstruct
    public void init(){
        initService.dbInit1(bCryptPasswordEncoder);
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    public static class InitService {

        private final EntityManager em;

        public void dbInit1(BCryptPasswordEncoder bCryptPasswordEncoder) {
//            System.out.println("Init1" + this.getClass());
//            String encodedPassword = bCryptPasswordEncoder.encode("test1234");
//            Member member = Member.builder().
//                    username("onyou")
//                    .email("dhsdb1541@naver.com")
//                    .password(encodedPassword)
//                    .build();
//            em.persist(member);

        }

    }
}
