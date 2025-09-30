package com.service.User.services;


import com.service.User.dtos.*;

public interface AuthService {
    public UserResponseDto register(RegisterRequest req);
    public AuthResponse login(LoginRequest req);
    public AuthResponse refreshToken(RefreshTokenRequest req);
    void logout(LogoutRequest req);
}
