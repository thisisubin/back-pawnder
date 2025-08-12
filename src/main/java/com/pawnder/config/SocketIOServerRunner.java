package com.pawnder.config;

import com.corundumstudio.socketio.SocketIOServer;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


@Component
public class SocketIOServerRunner implements CommandLineRunner {

    private final SocketIOServer server;

    @Autowired
    public SocketIOServerRunner(SocketIOServer server) {
        this.server = server;
    }

    @Override
    public void run(String... args) throws Exception {
        server.start();
        System.out.println("Socket.IO 서버가 시작되었습니다. 포트: " + server.getConfiguration().getPort());
    }

    @PreDestroy
    public void stop() {
        if (server != null) {
            server.stop();
            System.out.println("Socket.IO 서버가 중지되었습니다.");
        }
    }
}
