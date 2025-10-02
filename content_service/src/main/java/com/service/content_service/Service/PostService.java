package com.service.content_service.Service;

import com.service.content_service.Dto.PostRequestDTO;
import com.service.content_service.Dto.PostResponseDTO;

import java.util.List;

public interface PostService {

    PostResponseDTO createPost(PostRequestDTO req);
    List<PostResponseDTO> getPostsByUser(Long userId);
    List<PostResponseDTO> getPublicPosts();
    PostResponseDTO getPostById(Long postId);
    void deletePost(Long postId, Long userId);
}
