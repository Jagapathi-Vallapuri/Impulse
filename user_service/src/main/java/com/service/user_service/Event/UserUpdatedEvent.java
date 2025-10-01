package com.service.user_service.Event;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserUpdatedEvent {
    private UUID id;
    private String fullName;
    private String profileImage;
    private String bio;
}
