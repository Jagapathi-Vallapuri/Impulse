package com.service.content_service.Service.Impl;

import com.service.content_service.Entity.Comment;
import com.service.content_service.Repository.CommentRepository;
import com.service.content_service.Repository.PostRepository;
import com.service.content_service.Service.CommentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commonRepository;
    private final PostRepository postRepository;

    @Override
    public Comment createComment(Long postID, Long useId, String content) {
        return null;
    }

    @Override
    public Optional<Comment> getCommentByID(Long id) {
        return Optional.empty();
    }

    @Override
    public List<Comment> getCommentsByPost(Long postId) {
        return List.of();
    }

    @Override
    public List<Comment> getCommentsByUser(Long userId) {
        return List.of();
    }

    @Override
    public Comment updateComment(Long id, String content) {
        return null;
    }

    @Override
    public void deleteComment(Long id) {

    }
}
