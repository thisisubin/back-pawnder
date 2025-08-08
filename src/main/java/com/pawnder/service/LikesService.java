package com.pawnder.service;

import com.pawnder.dto.LikeToggleResult;
import com.pawnder.entity.CommunityPost;
import com.pawnder.entity.Likes;
import com.pawnder.entity.User;
import com.pawnder.repository.CommunityPostRepository;
import com.pawnder.repository.LikesRepository;
import com.pawnder.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class LikesService {

    private final CommunityPostRepository communityPostRepository;
    private final UserRepository userRepository;
    private final LikesRepository likesRepository;
    private final NotificationService notificationService;

    // 좋아요 토글 메서드 (좋아요가 없으면 추가, 있으면 삭제)
    public LikeToggleResult toggleLike(Long postId, String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("로그인된 유저만 좋아요를 누를 수 있습니다."));

        CommunityPost communityPost = communityPostRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 글을 찾을 수 없습니다."));

        // 이미 좋아요를 눌렀는지 확인
        Optional<Likes> existingLike = likesRepository.findByUserAndCommunityPost(user, communityPost);

        if (existingLike.isPresent()) {
            // 이미 좋아요가 있으면 삭제
            likesRepository.delete(existingLike.get());
            log.info("좋아요 삭제: 사용자 {}, 게시글 {}", userId, postId);

            return new LikeToggleResult(false, countLike(postId));
        } else {
            // 좋아요가 없으면 추가
            Likes like = new Likes();
            like.setUser(user);
            like.setCommunityPost(communityPost);

            likesRepository.save(like);
            log.info("좋아요 추가: 사용자 {}, 게시글 {}", userId, postId);

            return new LikeToggleResult(true, countLike(postId));
        }
    }

    // 좋아요 추가 메서드 (중복 체크)
    public void saveLike(Long postId, String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("로그인된 유저만 좋아요를 누를 수 있습니다."));

        CommunityPost communityPost = communityPostRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 글을 찾을 수 없습니다."));

        // 이미 좋아요를 눌렀는지 확인
        Optional<Likes> existingLike = likesRepository.findByUserAndCommunityPost(user, communityPost);
        if (existingLike.isPresent()) {
            throw new IllegalArgumentException("이미 좋아요를 누른 게시글입니다.");
        }

        Likes like = new Likes();
        like.setUser(user);
        like.setCommunityPost(communityPost);

        likesRepository.save(like);
        notificationService.sendNotification(userId, like.getUser().getUserId(), like.getCommunityPost().getTitle() + "에 좋아요가 달렸습니다.", "LIKE");

        log.info("좋아요 추가: 사용자 {}, 게시글 {}", userId, postId);
    }

    // 좋아요 삭제 메서드
    public void deleteLike(Long postId, String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("로그인된 유저만 좋아요를 취소할 수 있습니다."));

        CommunityPost communityPost = communityPostRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 글을 찾을 수 없습니다."));

        Likes like = likesRepository.findByUserAndCommunityPost(user, communityPost)
                .orElseThrow(() -> new IllegalArgumentException("좋아요를 누르지 않은 게시글입니다."));

        likesRepository.delete(like);
        log.info("좋아요 삭제: 사용자 {}, 게시글 {}", userId, postId);
    }

    // 특정 게시글의 좋아요 개수 반환
    @Transactional(readOnly = true)
    public Long countLike(Long postId) {
        return likesRepository.countLikesByPostId(postId);
    }

    // 사용자가 특정 게시글에 좋아요를 눌렀는지 확인
    @Transactional(readOnly = true)
    public boolean isLikedByUser(Long postId, String userId) {
        User user = userRepository.findByUserId(userId)
                .orElse(null);

        if (user == null) {
            return false;
        }

        CommunityPost communityPost = communityPostRepository.findById(postId)
                .orElse(null);

        if (communityPost == null) {
            return false;
        }

        return likesRepository.findByUserAndCommunityPost(user, communityPost).isPresent();
    }

    // 사용자가 좋아요한 모든 게시글 ID 목록 반환
    @Transactional(readOnly = true)
    public java.util.List<Long> getLikedPostIdsByUser(String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        return likesRepository.findByUser(user).stream()
                .map(like -> like.getCommunityPost().getId())
                .toList();
    }
}
