package com.service.User.services.Impl;

import com.service.User.entities.User;
import com.service.User.services.JwtService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.UUID;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class JwtServiceImpl implements JwtService {
    private final SecretKey key;
    private static final long ACCESS_TOKEN_VALIDITY_MS = 1000L * 60 * 15; // 15 minutes
    private static final long REFRESH_TOKEN_VALIDITY_MS = 1000L * 60 * 60 * 24 * 7; // 7 days

    private final ConcurrentHashMap<String, Boolean> invalidatedTokens = new ConcurrentHashMap<>();

    public JwtServiceImpl(@Value("${jwt.secret:}") String jwtSecret) {
        if (jwtSecret == null || jwtSecret.isBlank()) {
            this.key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
            System.err.println("WARNING: JWT secret not provided; using ephemeral key. Tokens will not survive restart.");
        } else {
            byte[] bytes = jwtSecret.getBytes(java.nio.charset.StandardCharsets.UTF_8);
            this.key = Keys.hmacShaKeyFor(bytes);
        }
    }

    @Override
    public String generateAccessToken(User user) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(user.getId().toString())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusMillis(ACCESS_TOKEN_VALIDITY_MS)))
                .signWith(key)
                .compact();
    }

    @Override
    public String generateRefreshToken(User user) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(user.getId().toString())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusMillis(REFRESH_TOKEN_VALIDITY_MS)))
                .signWith(key)
                .compact();
    }

    @Override
    public UUID validateRefreshToken(String token) {
        if (invalidatedTokens.containsKey(token))
            throw new RuntimeException("Invalid refresh token");
        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return UUID.fromString(claims.getBody().getSubject());
        } catch (JwtException e) {
            throw new RuntimeException("Invalid refresh token");
        }
    }

    @Override
    public void invalidateRefreshToken(String token) {
        invalidatedTokens.put(token, true);
    }

    @Override
    public UUID validateAccessToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return UUID.fromString(claims.getBody().getSubject());
        } catch (JwtException e) {
            throw new RuntimeException("Invalid access token");
        }
    }
}
