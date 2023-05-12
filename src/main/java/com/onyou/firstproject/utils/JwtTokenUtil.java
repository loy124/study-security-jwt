package com.onyou.firstproject.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
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
