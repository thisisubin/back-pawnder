package com.pawnder.repository;

import com.pawnder.entity.CommunityPost;
import com.pawnder.entity.Likes;
import com.pawnder.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LikesRepository extends JpaRepository<Likes, Long> {

    @Query("SELECT COUNT(l) FROM Likes l WHERE l.communityPost.id = :postId")
    Long countLikesByPostId(@Param("postId") Long postId);

    // 사용자와 게시글로 좋아요 찾기
    Optional<Likes> findByUserAndCommunityPost(User user, CommunityPost communityPost);

    // 사용자가 좋아요한 모든 게시글 찾기
    List<Likes> findByUser(User user);

    // 특정 게시글의 모든 좋아요 찾기
    List<Likes> findByCommunityPost(CommunityPost communityPost);

    // 사용자가 특정 게시글에 좋아요를 눌렀는지 확인
    boolean existsByUserAndCommunityPost(User user, CommunityPost communityPost);
}
