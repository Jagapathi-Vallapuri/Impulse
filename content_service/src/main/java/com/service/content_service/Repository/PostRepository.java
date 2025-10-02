package com.service.content_service.Repository;

import com.service.content_service.Entity.Post;
import com.service.content_service.Entity.Visibility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByUserIdAndIsDeletedFalseOrderByCreatedAtDesc(Long userId);
    List<Post> findByVisibilityAndIsDeletedFalseOrderByCreatedATDesc(Visibility visibility);
}
