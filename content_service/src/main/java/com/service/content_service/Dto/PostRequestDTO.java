package com.service.content_service.Dto;

import java.util.List;
import com.service.content_service.Dto.MediaRequestDTO;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostRequestDTO {
    private Long userId;
    private String content;
    private String visibility;
    private List<MediaRequestDTO> mediaList;
}
