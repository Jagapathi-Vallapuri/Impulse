package com.service.content_service.Service.Impl;

import com.service.content_service.Dto.*;
import com.service.content_service.Entity.*;
import com.service.content_service.Repository.MediaRepository;
import com.service.content_service.Repository.PostMetadataRepository;
import com.service.content_service.Repository.PostRepository;
import com.service.content_service.Service.PostService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final MediaRepository mediaRepository;
    private final PostMetadataRepository metadataRepository;

        private final KafkaTemplate<String, String> kafkaTemplate;
    private static final String POST_EVENTS_TOPIC = "post-events";

    @Override
    @Transactional
    public PostResponseDTO createPost(PostRequestDTO req) {
        Post post = Post.builder()
                .userId(req.getUserId())
                .content(req.getContent())
                .visibility(Visibility.valueOf(req.getVisibility()))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isDeleted(false)
                .build();
        Post savedPost = postRepository.save(post);
        if(req.getMediaList() != null){
            List<Media> mediaList = req.getMediaList().stream()
                    .map(mediaReq->Media.builder()
                            .url(mediaReq.getUrl())
                            .type(MediaType.valueOf(mediaReq.getType()))
                            .post((savedPost))
                            .build())
                    .toList();
            mediaRepository.saveAll(mediaList);
            savedPost.setMediaList(mediaList);
        }

        PostMetadata metadata = PostMetadata.builder()
                .likeCount(0L)
                .commentCount(0L)
                .shareCount(0L)
                .post(savedPost)
                .build();
        metadataRepository.save(metadata);
        savedPost.setMetadata(metadata);

        kafkaTemplate.send(POST_EVENTS_TOPIC, "PostCreated:" + savedPost.getId());

        return mapToResponse(savedPost);
    }



    @Override
    public List<PostResponseDTO> getPostsByUser(Long userId) {
        return postRepository.findByUserIdAndIsDeletedFalseOrderByCreatedAtDesc(userId)
                .stream().map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<PostResponseDTO> getPublicPosts() {
                return postRepository.findByVisibilityAndIsDeletedFalseOrderByCreatedAtDesc(Visibility.PUBLIC)
                .stream().map(this::mapToResponse)
                .toList();
    }

    @Override
    public PostResponseDTO getPostById(Long postId) {
        return postRepository.findById(postId)
                .filter(post -> !post.isDeleted())
                .map(this::mapToResponse)
                .orElseThrow(()-> new RuntimeException("Post not found"));
    }

    @Override
    public void deletePost(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        if (!post.getUserId().equals(userId))
            throw new RuntimeException("Unauthorized to delete this post");

        post.setDeleted(true);
        post.setUpdatedAt(LocalDateTime.now());
        postRepository.save(post);

        kafkaTemplate.send(POST_EVENTS_TOPIC, "PostDeleted:" + postId);

    }

    private PostResponseDTO mapToResponse(Post savedPost) {
        return PostResponseDTO.builder()
                .id(savedPost.getId())
                .userId(savedPost.getUserId())
                .content(savedPost.getContent())
                .visibility(savedPost.getVisibility().name())
                .isDeleted(savedPost.isDeleted())
                .createdAt(savedPost.getCreatedAt())
                .updatedAt(savedPost.getUpdatedAt())
                .mediaList(savedPost.getMediaList() != null ? savedPost.getMediaList().stream()
                        .map(media -> MediaResponseDTO.builder()
                                .id(media.getId())
                                .url(media.getUrl())
                                .type(media.getType().name())
                                .build()
                        ).toList() : null)
                .metadata(savedPost.getMetadata() != null ? PostMetadataDTO.builder()
                        .likeCount(savedPost.getMetadata().getLikeCount())
                                .commentCount(savedPost.getMetadata().getCommentCount())
                                .shareCount(savedPost.getMetadata().getShareCount())
                                .build() : null)
                .build();
    }
}
