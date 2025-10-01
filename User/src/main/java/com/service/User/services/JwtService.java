package com.service.User.services;

import com.service.User.entities.User;

import java.util.UUID;

public interface JwtService {
    String generateAccessToken(User user);

    String generateRefreshToken(User user);

    UUID validateRefreshToken(String token);

    UUID validateAccessToken(String token);

    void invalidateRefreshToken(String token);
}
