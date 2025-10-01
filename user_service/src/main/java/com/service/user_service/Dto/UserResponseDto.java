package com.service.user_service.Dto;

import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponseDto {
    private UUID id;
    private String username;
    private String email;
    private String fullname;
    private String bio;
    private String profileImage;
    private String location;
    private LocalDate dateOfBirth;
}
