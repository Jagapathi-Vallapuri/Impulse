package com.service.User.services.Impl;

import com.service.User.dtos.UpdateProfileRequest;
import com.service.User.dtos.UserResponseDto;
import com.service.User.entities.User;
import com.service.User.entities.UserProfile;
import com.service.User.entities.UserStatus;
import com.service.User.events.UserEventPublisherImpl;
import com.service.User.repositories.UserProfileRepository;
import com.service.User.repositories.UserRepository;
import com.service.User.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserProfileRepository profileRepository;
    private final UserEventPublisherImpl eventPublisher;

    @Override
    public UserResponseDto getUserById(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new RuntimeException("User not found"));
        UserProfile profile = profileRepository.findByUserId(userId)
                .orElseThrow(()-> new RuntimeException("Profile not found"));

        return mapToDto(user, profile);
    }

    @Override
    public UserResponseDto updateProfile(UUID userId, UpdateProfileRequest req) {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new RuntimeException("User not found"));
        UserProfile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        if (req.getFullName() != null) profile.setFullname(req.getFullName());
        if (req.getBio() != null) profile.setBio(req.getBio());
        if (req.getProfileImage() != null) profile.setProfileImage(req.getProfileImage());
        if (req.getLocation() != null) profile.setLocation(req.getLocation());
        if (req.getDateOfBirth() != null) profile.setDateOfBirth(req.getDateOfBirth());
        profileRepository.save(profile);
        eventPublisher.publishUserUpdated(profile);
        return mapToDto(user, profile);
    }

    @Override
    public void deleteUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setStatus(UserStatus.DELETED);
        userRepository.save(user);

        eventPublisher.publishUserDeleted(userId);
    }

    @Override
    public List<UserResponseDto> searchUsers(String query) {
        return userRepository.searchByUsername(query).stream()
                .map(u -> profileRepository.findByUserId(u.getId())
                        .map(p -> mapToDto(u, p))
                        .orElse(UserResponseDto.builder()
                                .id(u.getId())
                                .username(u.getUsername())
                                .email(u.getEmail())
                                .build()))
                .collect(Collectors.toList());
    }

    private UserResponseDto mapToDto(User user, UserProfile profile){
        return UserResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .fullname(profile.getFullname())
                .bio(profile.getBio())
                .location(profile.getLocation())
                .dateOfBirth(profile.getDateOfBirth())
                .build();
    }
}
