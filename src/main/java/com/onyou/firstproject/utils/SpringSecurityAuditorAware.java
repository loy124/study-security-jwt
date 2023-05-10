package com.onyou.firstproject.utils;

import com.onyou.firstproject.config.auth.PrincipalDetails;
import com.onyou.firstproject.member.entity.Member;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SpringSecurityAuditorAware implements AuditorAware<Long> {

    @Override
    public Optional<Long> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        if(!(authentication.getPrincipal() instanceof String)){
            PrincipalDetails principal = (PrincipalDetails) (authentication.getPrincipal());
            return Optional.of(principal.getMember().getId());
        }




//        if(authentication.getPrincipal() instanceof Member){
//
//
//            return Optional.of(userDetails.getMember().getId().toString());
//        }

        return Optional.empty();
//        if(authentication.getPrincipal() != null)




//        String email = JwtTokenUtil.getEmailFromToken((String) authentication.getCredentials());
//        return Optional.of("check");
    }
}
