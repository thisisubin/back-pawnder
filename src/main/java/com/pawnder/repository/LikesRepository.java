package com.pawnder.repository;

import com.pawnder.entity.Likes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LikesRepository extends JpaRepository<Likes, Long> {

    @Query("SELECT COUNT(l) FROM Likes l WHERE l.communityPost.id = :postId")
    Long countLikesByPostId(@Param("postId") Long postId);
}
