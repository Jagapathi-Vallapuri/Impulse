package com.service.content_service.Dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostMetadataDTO {
    private Long likeCount;
    private Long commentCount;
    private Long shareCount;
}
