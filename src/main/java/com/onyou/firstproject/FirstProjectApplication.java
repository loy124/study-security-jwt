package com.onyou.firstproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;
import java.util.UUID;

@SpringBootApplication
@EnableJpaAuditing
public class FirstProjectApplication {

	@Bean
	public BCryptPasswordEncoder encodePassword(){
		return new BCryptPasswordEncoder();
	}

	//실무에서는 스프링 시큐리티 정보를 엮는다

	//해당 Bean을 등록하면 CreatedBy를 자동으로 주입을 해준다.
	@Bean
	public AuditorAware<String> auditorProvider() {
		return () -> Optional.of(UUID.randomUUID().toString());
	}
	public static void main(String[] args) {
		SpringApplication.run(FirstProjectApplication.class, args);
	}

}
