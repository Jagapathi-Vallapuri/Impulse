package com.service.User.services.Impl;

import com.service.User.entities.User;
import com.service.User.services.JwtService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.UUID;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class JwtServiceImpl implements JwtService {
    private final SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private final long accessTokenValidity = 1000*60*15;
    private final long refreshTokenValidity = 1000L * 60 * 60 * 24 * 7;

    private final ConcurrentHashMap<String, Boolean> invalidatedTokens = new ConcurrentHashMap<>();

    @Override
    public String generateAccessToken(User user) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(user.getId().toString())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusMillis((accessTokenValidity))))
                .signWith(key)
                .compact();
    }

    @Override
    public String generateRefreshToken(User user) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(user.getId().toString())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusMillis(refreshTokenValidity)))
                .signWith(key)
                .compact();
    }

    @Override
    public UUID validateRefreshToken(String token) {
        if(invalidatedTokens.containsKey(token))
            throw new RuntimeException("Invalid refresh token");
        try{
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return UUID.fromString(claims.getBody().getSubject());
        }
        catch (JwtException e){
            throw new RuntimeException("Invalid refresh token");
        }
    }

    @Override
    public void invalidateRefreshToken(String token) {
        invalidatedTokens.put(token, true);
    }
}
