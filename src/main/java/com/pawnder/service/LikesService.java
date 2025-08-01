package com.pawnder.service;

import com.pawnder.entity.CommunityPost;
import com.pawnder.entity.Likes;
import com.pawnder.entity.User;
import com.pawnder.repository.CommunityPostRepository;
import com.pawnder.repository.LikesRepository;
import com.pawnder.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class LikesService {
    private final CommunityPostRepository communityPostRepository;
    private final UserRepository userRepository;
    private final LikesRepository likesRepository;

    //Like 저장 메서드
    public void saveLike(Long postId, String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("로그인된 유저만 좋아요를 누를 수 있습니다."));
        CommunityPost communityPost = communityPostRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 글을 찾을 수 없습니다."));


        Likes like = new Likes();
        like.setUser(user);
        like.setCommunityPost(communityPost);
        like.setLikedAt(LocalDateTime.now());

        likesRepository.save(like);
    }

    // 특정 게시글의 좋아요 개수 반환
    public Long countLike(Long postId) {
        return likesRepository.countLikesByPostId(postId);
    }

}
