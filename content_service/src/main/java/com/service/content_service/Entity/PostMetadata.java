package com.service.content_service.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "post_metadata")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long likeCount = 0L;
    private Long commentCount = 0L;
    private Long shareCount = 0L;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;
}
