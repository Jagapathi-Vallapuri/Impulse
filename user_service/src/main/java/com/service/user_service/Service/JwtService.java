package com.service.user_service.Service;



import com.service.user_service.Entity.User;

import java.util.UUID;

public interface JwtService {
    String generateAccessToken(User user);

    String generateRefreshToken(User user);

    UUID validateRefreshToken(String token);

    UUID validateAccessToken(String token);

    void invalidateRefreshToken(String token);
}
