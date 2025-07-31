package com.pawnder.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pawnder.config.SessionUtil;
import com.pawnder.dto.CommunityPostDto;
import com.pawnder.entity.User;
import com.pawnder.repository.PetRepository;
import com.pawnder.repository.UserRepository;
import com.pawnder.service.CommunityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;
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
    @Operation(summary = "커뮤니티 글 작성")
    @PostMapping("/createPost")
    public ResponseEntity<Map<String, Object>> createPost(
            HttpSession session,
            @RequestPart("imgUrl") MultipartFile imgUrl,
            @RequestPart("post") CommunityPostDto postDto) throws IOException {
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
            communityService.savePost(postDto, userId, imgUrl);

            response.put("success", false);
            response.put("message", "등록 중 오류가 발생했습니다.");
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
    @PutMapping(value = "/description/{postId}/edit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> editPost(
            @PathVariable Long postId,
            @RequestParam("userId") String userId,
            @RequestPart("communityPost") CommunityPostDto communityPostDto,
            @RequestPart(value = "imgurlContent", required = false) MultipartFile imgurlContent
    ) {
        Map<String, Object> response = new HashMap<>();
        try {
            boolean result = communityService.editPost(postId, userId, communityPostDto, imgurlContent);
            response.put("success", result);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @Operation(summary = "커뮤니티 글 상세 삭제")
    @DeleteMapping("/description/{postId}/delete")
    public ResponseEntity<Map<String, Object>> deletePost(@PathVariable Long postId, @RequestParam ("userId") String userId){
        Map<String, Object> response = new HashMap<>();
        try {
            boolean result = communityService.deletePost(postId, userId);
            response.put("success", result);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }


    @Operation(summary = "좋아요 달기")
    @PostMapping("/like")
    public ResponseEntity<?> likePost() {
        return ResponseEntity.ok("좋아요 완료");
    }

    @Operation(summary = "댓글 달기")
    @PostMapping("/comment")
    public ResponseEntity<?> commentPost() {
        return ResponseEntity.ok("댓글 등록 완료");
    }
}
