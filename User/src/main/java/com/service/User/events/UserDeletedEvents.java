package com.service.User.events;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDeletedEvents {
    private UUID id;
    private LocalDateTime deletedAt;
}
