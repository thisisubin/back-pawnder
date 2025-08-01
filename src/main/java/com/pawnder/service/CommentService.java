package com.pawnder.service;

import com.pawnder.dto.CommentDto;
import com.pawnder.dto.CommunityPostDto;
import com.pawnder.entity.Comment;
import com.pawnder.entity.CommunityPost;
import com.pawnder.entity.User;
import com.pawnder.repository.CommentRepository;
import com.pawnder.repository.CommunityPostRepository;
import com.pawnder.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {
    private final CommunityPostRepository communityPostRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    //1. Comment 저장 메서드
    public void saveComment(CommentDto commentDto, Long postId, String userId) {
        //1-1. 유저를 확인
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("로그인된 유저만 댓글 작성이 가능합니다."));

        //1-2. 게시글 확인
        CommunityPost communityPost = communityPostRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 글을 찾을 수 없습니다."));

        //1-3. Dto -> Entity 변환
        Comment comment = new Comment();
        comment.setCommunityPost(communityPost);
        comment.setUser(user);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setContent(commentDto.getContent());

        commentRepository.save(comment);
    }

    //2. 모든 댓글 GET 메서드
    public List<CommentDto> getCommentsByPostId(Long postId) {
        List<Comment> comments = commentRepository.findByCommunityPostIdOrderByCreatedAtDesc(postId);

        return comments.stream()
                .map(comment -> {
                    CommentDto commentDto = new CommentDto();
                    commentDto.setCommunityPostDto(CommunityPostDto.fromEntity(comment.getCommunityPost()));
                    commentDto.setContent(comment.getContent());
                    commentDto.setUserId(comment.getUser().getUserId());
                    commentDto.setCreatedAt(comment.getCreatedAt());
                    return commentDto;
                })
                .collect(Collectors.toList());
    }


}
