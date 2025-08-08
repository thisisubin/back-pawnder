package com.pawnder.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LikeToggleResult {

    private boolean isLiked;
    private Long likeCount;
}
