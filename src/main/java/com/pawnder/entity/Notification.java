package com.pawnder.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "sender", nullable = false)
    private String sender;

    @Column(name = "message", nullable = false, length = 500)
    private String message;

    @Column(name = "notification_type")
    private String notificationType; // ADOPTION_REQUEST, ADOPTION_RESULT, COMMENT, LIKE, ABANDONED_PET, SYSTEM

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "is_read", nullable = false)
    private boolean isRead = false;

    @Column(name = "related_id")
    private String relatedId; // 관련된 게시글, 입양신청 등의 ID

    @Column(name = "target_url")
    private String targetUrl; // 알림 클릭 시 이동할 URL

    @Column(name = "read_at")
    private LocalDateTime readAt; // 읽은 시간

    @Builder
    public Notification(String userId, String sender, String message, String notificationType, String relatedId, String targetUrl) {
        this.userId = userId;
        this.sender = sender;
        this.message = message;
        this.notificationType = notificationType;
        this.relatedId = relatedId;
        this.targetUrl = targetUrl;
        this.createdAt = LocalDateTime.now();
        this.isRead = false;
    }

    public Notification(String userId, String sender, String message, String notificationType) {
        this.userId = userId;
        this.sender = sender;
        this.message = message;
        this.notificationType = notificationType;
        this.createdAt = LocalDateTime.now();
        this.isRead = false;
    }

    public Notification(String userId, String sender, String message, String notificationType, String relatedId) {
        this.userId = userId;
        this.sender = sender;
        this.message = message;
        this.notificationType = notificationType;
        this.relatedId = relatedId;
        this.createdAt = LocalDateTime.now();
        this.isRead = false;
    }
}
