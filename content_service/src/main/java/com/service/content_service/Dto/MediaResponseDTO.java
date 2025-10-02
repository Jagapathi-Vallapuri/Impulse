package com.service.content_service.Dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MediaResponseDTO {
    private Long id;
    private String url;
    private String type;
}
