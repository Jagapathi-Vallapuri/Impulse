package com.service.User.events;

import com.service.User.entities.UserProfile;

import java.util.UUID;

public interface UserEventPublisher {
    void publishUserCreated(UserCreatedEvent event);
    void publishUserUpdated(UserProfile profile);
    void publishUserDeleted(UUID userId);
}
