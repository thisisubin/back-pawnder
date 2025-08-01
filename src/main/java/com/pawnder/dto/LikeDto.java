package com.pawnder.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class LikeDto {
    private CommunityPostDto communityPostDto;
    private LocalDateTime createdAt;
}
