package com.service.user_service.Event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.service.user_service.Entity.UserProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserEventPublisherImpl implements UserEventPublisher {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    private static final String TOPIC_USER_CREATED = "user.created";
    private static final String TOPIC_USER_UPDATED = "user.updated";
    private static final String TOPIC_USER_DELETED = "user.deleted";

    @Override
    public void publishUserCreated(UserCreatedEvent event) {
        publishEvent(TOPIC_USER_CREATED, event);
    }

    @Override
    public void publishUserUpdated(UserProfile profile) {
        UserUpdatedEvent event = UserUpdatedEvent.builder()
                .id(profile.getUser().getId())
                .bio(profile.getBio())
                .fullName(profile.getFullname())
                .profileImage(profile.getProfileImage())
                .build();
        publishEvent(TOPIC_USER_UPDATED, event);
    }

    @Override
    public void publishUserDeleted(UUID userId) {
        UserDeletedEvents event = UserDeletedEvents.builder()
                .id(userId)
                .build();
        publishEvent(TOPIC_USER_DELETED, event);
    }

    private void publishEvent(String topic, Object event) {
        try {
            String msg = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(topic, msg);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize event", e);
        }
    }
}
