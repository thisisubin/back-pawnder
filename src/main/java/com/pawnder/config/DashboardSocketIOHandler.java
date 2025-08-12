package com.pawnder.config;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DashboardSocketIOHandler {

    private static final String DASHBOARD_ROOM = "dashboard";
    private static final Logger log = LoggerFactory.getLogger(DashboardSocketIOHandler.class);

    private final SocketIOServer socketIOServer;

    public DashboardSocketIOHandler(SocketIOServer socketIOServer) {
        this.socketIOServer = socketIOServer;
    }

    @PostConstruct
    public void setUpListeners() {
        // 클라이언트 연결 시 간단한 확인 메시지 전송
        socketIOServer.addConnectListener(client -> {
            log.debug("Socket connected: {}", safeClientId(client));
            client.sendEvent("connected", "connected");
        });

        socketIOServer.addDisconnectListener(client
                -> log.debug("Socket disconnected: {}", safeClientId(client))
        );

        // 대시보드 참가 이벤트 처리
        socketIOServer.addEventListener("join_dashboard", String.class, (client, role, ackSender) -> {
            log.debug("join_dashboard received. role={}, clientId={}", role, safeClientId(client));
            client.joinRoom(DASHBOARD_ROOM);
            client.sendEvent("dashboard_joined", "joined");
        });
    }

    public void broadcastDashboardUpdate(String eventName, Map<String, Object> payload) {
        try {
            // 대시보드 룸에 먼저 전송. 참여자가 없으면 전체 브로드캐스트로 폴백
            if (!socketIOServer.getRoomOperations(DASHBOARD_ROOM).getClients().isEmpty()) {
                socketIOServer.getRoomOperations(DASHBOARD_ROOM).sendEvent(eventName, payload);
            } else {
                socketIOServer.getBroadcastOperations().sendEvent(eventName, payload);
            }
            log.debug("Sent event '{}' with payload {}", eventName, payload);
        } catch
        (Exception e) {
            log.warn("Failed to send socket event '{}': {}", eventName, e.getMessage());
        }
    }

    private String safeClientId(SocketIOClient client) {
        return client == null || client.getSessionId() == null ? "unknown" : client.getSessionId().toString();
    }
}
