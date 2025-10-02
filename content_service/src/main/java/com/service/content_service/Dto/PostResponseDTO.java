package com.service.content_service.Dto;

import com.service.content_service.Entity.PostMetadata;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostResponseDTO {
    private Long id;
    private Long userId;
    private String content;
    private boolean isDeleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<MediaResponseDTO> mediaList;
    private PostMetadataDTO metadata;
    private List<CommentResponseDTO> comments;
    private List<LikeResponseDTO> likes;
}
