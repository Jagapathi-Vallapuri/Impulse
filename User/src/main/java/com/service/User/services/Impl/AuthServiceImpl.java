package com.service.User.services.Impl;

import com.service.User.dtos.AuthResponse;
import com.service.User.dtos.LoginRequest;
import com.service.User.dtos.RegisterRequest;
import com.service.User.dtos.UserResponseDto;
import com.service.User.entities.User;
import com.service.User.entities.UserProfile;
import com.service.User.entities.UserStatus;
import com.service.User.events.UserEventPublisherImpl;
import com.service.User.repositories.UserProfileRepository;
import com.service.User.repositories.UserRepository;
import com.service.User.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final UserEventPublisherImpl eventPublisher;
    private final PasswordEncoder passwordEncoder;
    private final JwtServiceImpl jwtService;


    @Override
    public UserResponseDto register(RegisterRequest req) {
        if(userRepository.existsByEmail(req.getEmail()))
            throw new RuntimeException("Email already registered");

        if(userRepository.existsByUsername(req.getUsername()))
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
        return mapToDto(savedUser, profile);
    }

    @Override
    public AuthResponse login(LoginRequest req) {
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new RuntimeException("No matching username"));
        if(!passwordEncoder.matches(req.getPassword(), user.getPasswordHash()))
            throw new RuntimeException("Password Mismatch");
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return new AuthResponse(accessToken, refreshToken);
    }

    @Override
    public AuthResponse refreshToken(RefreshTokenRequest req) {
        return null;
    }

    @Override
    public void logout(LogoutRequest req) {
        jwtService.invalidateRefreshToken(req.getRefreshToken());
    }

    private UserResponseDto mapToDto(User user, UserProfile profile){
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
