package com.service.User.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProfileRequest {
    private String fullName;
    private String bio;
    private String profileImage;
    private String location;
    private LocalDate dateOfBirth;
}
