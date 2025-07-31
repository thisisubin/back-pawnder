package com.pawnder.dto;

import com.pawnder.constant.PostType;
import com.pawnder.entity.CommunityPost;
import com.pawnder.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@Schema(description = "커뮤니티 글 작성 DTO")
public class CommunityPostDto {

    private Long id;
    private PostType postType;
    private String title;
    private String userId;
    private String imgUrlContent;
    private String strContent;
    private LocalDateTime createdAt;

    public static CommunityPostDto fromEntity(CommunityPost post) {
        CommunityPostDto dto = new CommunityPostDto();
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setStrContent(post.getStrContent());
        dto.setImgUrlContent(post.getImgUrlContent());
        dto.setPostType(post.getPostType());
        dto.setUserId(post.getUser().getUserId());
        dto.setCreatedAt(post.getCreatedAt());
        return dto;
    }
}
