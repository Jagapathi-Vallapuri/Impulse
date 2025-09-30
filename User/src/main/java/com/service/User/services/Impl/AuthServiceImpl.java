package com.service.User.services.Impl;

import com.service.User.dtos.*;
import com.service.User.entities.User;
import com.service.User.entities.UserProfile;
import com.service.User.entities.UserStatus;
import com.service.User.events.UserCreatedEvent;
import com.service.User.events.UserEventPublisher;
import com.service.User.repositories.UserProfileRepository;
import com.service.User.repositories.UserRepository;
import com.service.User.services.AuthService;
import com.service.User.services.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final UserEventPublisher eventPublisher;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    @Transactional
    public UserResponseDto register(RegisterRequest req) {
        Objects.requireNonNull(req, "RegisterRequest must not be null");
        if (req.getEmail() == null || req.getEmail().isBlank())
            throw new IllegalArgumentException("Email is required");
        if (req.getUsername() == null || req.getUsername().isBlank())
            throw new IllegalArgumentException("Username is required");
        if (req.getPassword() == null || req.getPassword().isBlank())
            throw new IllegalArgumentException("Password is required");

        if (userRepository.existsByEmail(req.getEmail()))
            throw new RuntimeException("Email already registered");

        if (userRepository.existsByUsername(req.getUsername()))
            throw new RuntimeException("Username already taken");

        User user = User.builder()
                .username(req.getUsername())
                .email(req.getEmail())
                .passwordHash(passwordEncoder.encode(req.getPassword()))
                .status(UserStatus.ACTIVE)
                .build();

        User savedUser = userRepository.save(user);
        UserProfile profile = UserProfile.builder()
                .user(savedUser)
                .build();
        userProfileRepository.save(profile);

        eventPublisher.publishUserCreated(
                UserCreatedEvent.builder()
                        .id(savedUser.getId())
                        .email(savedUser.getEmail())
                        .username(savedUser.getUsername())
                        .createdAt(LocalDateTime.now())
                        .build());

        return mapToDto(savedUser, profile);
    }

    @Override
    public AuthResponse login(LoginRequest req) {
        Objects.requireNonNull(req, "LoginRequest must not be null");
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));
        if (user.getStatus() != UserStatus.ACTIVE)
            throw new RuntimeException("User is not active");
        if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash()))
            throw new RuntimeException("Invalid credentials");
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return new AuthResponse(accessToken, refreshToken);
    }

    @Override
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        Objects.requireNonNull(request, "RefreshTokenRequest must not be null");
        UUID userId = jwtService.validateRefreshToken(request.getRefreshToken());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        jwtService.invalidateRefreshToken(request.getRefreshToken());
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return new AuthResponse(accessToken, refreshToken);
    }

    @Override
    public void logout(LogoutRequest req) {
        jwtService.invalidateRefreshToken(req.getRefreshToken());
    }

    private UserResponseDto mapToDto(User user, UserProfile profile) {
        return UserResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullname(profile.getFullname())
                .bio(profile.getBio())
                .profileImage(profile.getProfileImage())
                .location(profile.getLocation())
                .dateOfBirth(profile.getDateOfBirth())
                .build();
    }
}
