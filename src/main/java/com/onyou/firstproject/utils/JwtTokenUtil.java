package com.onyou.firstproject.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.http.HttpStatus;

import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

public class JwtTokenUtil {

    public static Long accessExpireTimeMs = 1000 * 60 * 60l; // 1시간
    public static Long refreshExpireTimeMs = 1000 * 60 * 60l * 24 * 14; //24시간 * 14

    public static String getEmail(String token, String secretKey){
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseClaimsJws(token)
                .getBody().get("email", String.class);
    }

    public static boolean isExpired(String token, String secretKey){
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
//                .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration()
                    .before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    public static boolean isWithinOneWeek(String token, String secretKey) {
        JwtParser jwtParser = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
                .build();
        Jws<Claims> claimsJws = jwtParser.parseClaimsJws(token);
        Date expiration = claimsJws.getBody().getExpiration();
        Instant now = Instant.now();
        Instant expirationInstant = expiration.toInstant();
        Instant oneWeekFromNow = now.plusSeconds(7 * 24 * 60 * 60);

        return expirationInstant.isBefore(oneWeekFromNow);
    }
//    public static boolean isWithinOneWeek(String token, String secretKey){
//        try {
//            Date now = new Date();
//            Date oneWeeksAgo = new Date(now.getTime() - TimeUnit.DAYS.toMillis(7));
//            Date expirationDate = Jwts.parserBuilder()
//                    .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
////                .setSigningKey(secretKey)
//                    .build()
//                    .parseClaimsJws(token)
//                    .getBody()
//                    .getExpiration();
//
//            return expirationDate.after(oneWeeksAgo) && expirationDate.before(now);
//        } catch (Exception e) {
//            return false;
//        }
//    }

    public static String createToken(String email, String key, long expireTimeMs){
        Claims claims = Jwts.claims();
        claims.put("email", email);
        SecretKey secretKey = Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expireTimeMs))
                .signWith(secretKey, SignatureAlgorithm.HS256)
//                .signWith(SignatureAlgorithm.HS256, key) // Deprecated
                .compact();


    }


}

