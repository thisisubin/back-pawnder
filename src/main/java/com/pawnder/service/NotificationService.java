package com.pawnder.service;

import com.pawnder.dto.NotificationDto;
import com.pawnder.entity.Notification;
import com.pawnder.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    // 알림 타입별 목적지 결정
    private String getDestinationByType(String userId, String notificationType) {
        switch (notificationType) {
            case "ABANDONED_PET":
                return "/topic/user/abandoned/" + userId;
            case "ADOPTION_REQUEST":
                return "/topic/user/" + userId + "/adoption";
            case "ADOPTION_RESULT":
                return "/topic/admin/adoption/alerts";
            case "COMMENT":
                return "/topic/user/" + userId + "/community/comment";
            case "LIKE":
                return "/topic/user/" + userId + "/community/like";
            case "ADMIN_ALERT":
                return "/topic/admin/abandoned/alerts";
            default:
                return "/topic/user/" + userId + "/notifications";
        }
    }

    // 알림 저장 및 실시간 전송
    @Transactional
    public void sendNotification(String userId, String sender, String message, String notificationType) {
        // 데이터베이스에 알림 저장
        Notification notification = new Notification(userId, sender, message, notificationType);
        notificationRepository.save(notification);

        // 실시간 알림 전송
        NotificationDto dto = NotificationDto.builder()
                .userId(userId)
                .notificationType(notificationType)
                .message(message)
                .createdAt(LocalDateTime.now())
                .build();
        String destination = getDestinationByType(userId, notificationType);

        log.info("[알림 전송] 유저({})에게 알림 전송: {}, destination: {}", userId, dto, destination);
        messagingTemplate.convertAndSend(destination, dto);
    }

    /**
     * 사용자의 모든 알림 조회
     */
    @Transactional(readOnly = true)
    public List<NotificationDto> getUserNotifications(String userId) {
        List<Notification> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return notifications.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * 사용자의 읽지 않은 알림 조회
     */
    @Transactional(readOnly = true)
    public List<NotificationDto> getUnreadNotifications(String userId) {
        List<Notification> notifications = notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);
        return notifications.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * 읽지 않은 알림 개수 조회
     */
    @Transactional(readOnly = true)
    public long getUnreadCount(String userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    /**
     * 특정 알림을 읽음으로 표시
     */
    @Transactional
    public void markAsRead(Long notificationId) {
        notificationRepository.markAsRead(notificationId);
    }

    /**
     * 사용자의 모든 알림을 읽음으로 표시
     */
    @Transactional
    public void markAllAsRead(String userId) {
        notificationRepository.markAllAsRead(userId);
    }

    /**
     * Entity를 DTO로 변환
     */
    private NotificationDto convertToDto(Notification notification) {
        return NotificationDto.builder()
                .id(notification.getId())
                .userId(notification.getUserId())
                .notificationType(notification.getNotificationType())
                .message(notification.getMessage())
                .targetUrl(notification.getTargetUrl())
                .isRead(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .readAt(notification.getReadAt())
                .build();
    }
}
