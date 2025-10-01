package com.service.user_service.Event;



import com.service.user_service.Entity.UserProfile;

import java.util.UUID;

public interface UserEventPublisher {
    void publishUserCreated(UserCreatedEvent event);

    void publishUserUpdated(UserProfile profile);

    void publishUserDeleted(UUID userId);
}
