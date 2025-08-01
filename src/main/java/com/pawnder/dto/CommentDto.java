package com.pawnder.dto;

import com.pawnder.entity.Comment;
import com.pawnder.entity.User;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CommentDto {
    private String userId;
    private CommunityPostDto communityPostDto;
    private String content;
    private LocalDateTime createdAt;
}
