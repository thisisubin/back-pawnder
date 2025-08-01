package com.pawnder.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.pawnder.constant.PostType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Slf4j
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class CommunityPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private PostType postType; //글 타입
    /*
    REVIEW,         //입양 후기
    TEMP_PROTECT,   //임시 보호 입양 홍보
    SHOW_OFF        //내 반려견 자랑
     */

    private String title; //글 제목

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();


    @Lob
    @Column(name = "img_content", columnDefinition = "MEDIUMBLOB")
    private String imgUrlContent; //이미지
    //나중에 배포하기 전에 S3와 연동해서 이미지 파일은 S3에 업로드, S3 URL을 DB에 저장

    @Lob
    @Column(name = "str_content", columnDefinition = "LONGTEXT")
    private String strContent; //글 내용


    //유저랑 매핑
    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;

    //삭제시 댓글과 좋아요들이 같이 삭제됨
    @OneToMany(mappedBy = "communityPost", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "communityPost", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Likes> likes = new ArrayList<>();
}
