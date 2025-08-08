package com.pawnder.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDto {
    private Long id;
    private String userId;
    private String notificationType;
    private String message;
    private String targetUrl;
    private boolean isRead;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;
} 