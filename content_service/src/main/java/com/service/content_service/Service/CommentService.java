package com.service.content_service.Service;

import com.service.content_service.Entity.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentService {
    Comment createComment(Long postID, Long useId, String content);
    Optional<Comment> getCommentByID(Long id);
    List<Comment> getCommentsByPost(Long postId);
    List<Comment> getCommentsByUser(Long userId);
    Comment updateComment(Long id, String content);
    void deleteComment(Long id);
}
