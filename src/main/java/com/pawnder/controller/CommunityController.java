package com.pawnder.controller;

import co.elastic.clients.elasticsearch.nodes.Http;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pawnder.config.SessionUtil;
import com.pawnder.dto.CommentDto;
import com.pawnder.dto.CommunityPostDto;
import com.pawnder.dto.LikeDto;
import com.pawnder.entity.Comment;
import com.pawnder.entity.User;
import com.pawnder.repository.LikesRepository;
import com.pawnder.repository.PetRepository;
import com.pawnder.repository.UserRepository;
import com.pawnder.service.CommentService;
import com.pawnder.service.CommunityService;
import com.pawnder.service.LikesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/community")
@Tag(name = "Community", description = "커뮤니티 관련 API")
public class CommunityController {
    private final CommunityService communityService;
    private final UserRepository userRepository;
    private final PetRepository petRepository;
    private final CommentService commentService;
    private final LikesService likesService;
    private final LikesRepository likesRepository;

    @Operation(summary = "커뮤니티 글 작성")
    @PostMapping("/createPost")
    public ResponseEntity<Map<String, Object>> createPost(
            HttpSession session,
            @RequestPart(value = "imgUrlContent", required = false) MultipartFile imgUrlContent,
            @RequestPart("communityPost") CommunityPostDto communityPostDto) throws IOException {
        Map<String, Object> response = new HashMap<>();

        //SessionUtil에서 로그인 세션 가져오기
        String userId = SessionUtil.getLoginUserId(session);
        if (userId == null) {
            response.put("success", false);
            response.put("message", "로그인이 필요합니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        try {
            // 포스트 저장
            communityService.savePost(communityPostDto, userId, imgUrlContent);

            response.put("success", true);
            response.put("message", "게시글이 성공적으로 등록되었습니다.");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("게시글 등록 중 오류 발생", e);
            response.put("success", false);
            response.put("message", "등록 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Operation(summary = "커뮤니티 전체 글 조회")
    @GetMapping("/posts")
    public ResponseEntity<List<CommunityPostDto>> getAllPosts() {
        List<CommunityPostDto> postList = communityService.getAllPosts();
        return ResponseEntity.ok(postList);
    }


    @Operation(summary = "커뮤니티 상세 조회")
    @GetMapping("/description/{postId}")
    public ResponseEntity<?> getCommunityPost(@PathVariable Long postId) {
        try {
            CommunityPostDto postDto = communityService.getPostDetail(postId); // 서비스에서 조회
            return ResponseEntity.ok(postDto);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 게시글을 찾을 수 없습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("오류 발생: " + e.getMessage());
        }
    }

    @Operation(summary = "커뮤니티 글 상세 수정")
    @PostMapping(value = "/description/{postId}/edit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> editPost(
            @PathVariable Long postId,
            @RequestPart("communityPost") CommunityPostDto communityPostDto,
            @RequestPart(value = "imgUrlContent", required = false) MultipartFile imgUrlContent
    ) {
        Map<String, Object> response = new HashMap<>();
        try {
            boolean result = communityService.editPost(postId, communityPostDto, imgUrlContent);
            response.put("success", result);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @Operation(summary = "커뮤니티 글 상세 삭제")
    @DeleteMapping("/description/{postId}/delete")
    public ResponseEntity<Map<String, Object>> deletePost(@PathVariable Long postId) {
        Map<String, Object> response = new HashMap<>();
        try {
            boolean result = communityService.deletePost(postId);
            response.put("success", result);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    //LikeService
    @Operation(summary = "좋아요 달기")
    @PostMapping("/like/{postId}")
    public ResponseEntity<?> postLike(@PathVariable Long postId, HttpSession session, @RequestBody LikeDto likeDto) {
        String userId = SessionUtil.getLoginUserId(session);
        likesService.saveLike(postId, userId);
        return ResponseEntity.ok("좋아요 완료");
    }

    //CommentService
    @Operation(summary = "댓글 달기")
    @PostMapping("/comment/{postId}")
    public ResponseEntity<Map<String, Object>> postComment(
            @PathVariable Long postId,
            HttpSession session,
            @RequestBody CommentDto commentDto // 아래에서 설명
    ) {
        Map<String, Object> response = new HashMap<>();
        try {
            String userId = SessionUtil.getLoginUserId(session);

            commentService.saveComment(commentDto, postId, userId);

            response.put("message", "댓글 등록 완료");
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            response.put("error", "오류 발생: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Operation(summary = "좋아요 개수 GET")
    @GetMapping("/like/{postId}/count")
    public ResponseEntity<?> getLikeAll(@PathVariable Long postId) {
        Long likeCount = likesService.countLike(postId);
        return ResponseEntity.ok(Map.of("postId", postId, "likeCount", likeCount));
    }

    @Operation(summary = "게시글 별 모든 댓글 내용 GET")
    @GetMapping("/comments/{postId}")
    public ResponseEntity<List<CommentDto>> getAllComments(@PathVariable Long postId) {
        List<CommentDto> comments = commentService.getCommentsByPostId(postId);
        return ResponseEntity.ok(comments);
    }

}
