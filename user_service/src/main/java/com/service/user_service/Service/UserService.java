package com.service.user_service.Service;



import com.service.user_service.Dto.UpdateProfileRequest;
import com.service.user_service.Dto.UserResponseDto;

import java.util.UUID;
import java.util.List;

public interface UserService {
    UserResponseDto getUserById(UUID userId);

    UserResponseDto updateProfile(UUID userId, UpdateProfileRequest req);

    void deleteUser(UUID userId);

    List<UserResponseDto> searchUsers(String query);
}
