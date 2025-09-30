package com.service.User.services;

import com.service.User.dtos.UpdateProfileRequest;
import com.service.User.dtos.UserResponseDto;

import java.util.UUID;
import java.util.List;

public interface UserService {
    UserResponseDto getUserById(UUID userId);

    UserResponseDto updateProfile(UUID userId, UpdateProfileRequest req);

    void deleteUser(UUID userId);

    List<UserResponseDto> searchUsers(String query);
}
