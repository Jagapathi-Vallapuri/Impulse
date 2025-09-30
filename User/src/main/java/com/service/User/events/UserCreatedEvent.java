package com.service.User.events;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserCreatedEvent {
    private UUID id;
    private String email;
    private String username;
    private LocalDateTime createdAt;
}
