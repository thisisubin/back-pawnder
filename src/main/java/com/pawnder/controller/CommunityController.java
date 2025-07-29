package com.pawnder.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/community")
@Tag(name = "Community", description = "커뮤니티 관련 API")
public class CommunityController {

    @Operation(summary = "입양 후기 작성")
    @PostMapping("/adopt/review/register")
    public ResponseEntity<?> registerAdoptedReview(@AuthenticationPrincipal UserDetails userDetails) {
        String userId = userDetails.getUsername();
        return ResponseEntity.ok("입양 후기 등록 완료");
    }

    @Operation(summary = "나의 반려견 자랑하기")
    @PostMapping("/mypet/register")
    public ResponseEntity<?> registerMyPetCommunity(@AuthenticationPrincipal UserDetails userDetails) {
        String userId = userDetails.getUsername();
        return ResponseEntity.ok("반려견 자랑 등록 완료");
    }

    @Operation(summary = "커뮤니티 글 조회")
    @GetMapping("/list")
    public ResponseEntity<?> getPostList() {
        return ResponseEntity.ok("리스트 조회 완료");
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
