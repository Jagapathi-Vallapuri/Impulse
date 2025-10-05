package com.service.user_service.Service;

import com.service.user_service.Dto.*;

public interface AuthService {
    public UserResponseDto register(RegisterRequest req);

    public AuthResponse login(LoginRequest req);

    public AuthResponse refreshToken(RefreshTokenRequest req);
}
