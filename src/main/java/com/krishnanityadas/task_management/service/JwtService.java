package com.krishnanityadas.task_management.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;


@Service
public class JwtService {
    private String secretString;


    JwtService(@Value("${jwt.secret}") String secretString) {
        this.secretString = secretString;
    }

    private String generateSecretString() {
        try {
            SecretKey key = Jwts.SIG.HS256.key().build();
            return Encoders.BASE64.encode(key.getEncoded());
        } catch (Exception e) {
            return e.getMessage();
        }
    }



    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 2))
                .signWith(getKey(), Jwts.SIG.HS256)
                .compact();
    }

    public SecretKey getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretString);
        SecretKey key = Keys.hmacShaKeyFor(keyBytes);
        return key;
    }

    
    public String extractUserName(String token) {
        // extract the username from jwt token
        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build().parseSignedClaims(token).getPayload();
    }


    public boolean validateToken(String token, UserDetails userDetails) {
        final String userName = extractUserName(token);
        return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public String extractTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }

        String fallbackToken = null;
        for (Cookie cookie : request.getCookies()) {
            if (!"token".equals(cookie.getName())) {
                continue;
            }

            String value = cookie.getValue();
            if (value != null && !value.isBlank()) {
                return value.trim();
            }

            // Keep a fallback in case only blank token cookies exist.
            fallbackToken = value;
        }

        return fallbackToken;
    }


}
