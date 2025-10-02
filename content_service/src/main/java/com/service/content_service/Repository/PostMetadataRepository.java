package com.service.content_service.Repository;

import com.service.content_service.Entity.PostMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostMetadataRepository extends JpaRepository<PostMetadata, Long> {
}
