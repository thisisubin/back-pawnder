package com.pawnder.repository;

import com.pawnder.dto.CommentDto;
import com.pawnder.entity.Comment;
import com.pawnder.entity.CommunityPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    // Repository
    List<Comment> findByCommunityPostIdOrderByCreatedAtDesc(Long postId);

}
