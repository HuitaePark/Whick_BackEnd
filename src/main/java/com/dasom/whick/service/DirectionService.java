package com.dasom.whick.service;

import com.dasom.whick.handler.DirectionWebSocketHandler;

import lombok.Getter;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class DirectionService {
    @Getter
    private volatile String currentDirection = "unknown";
    private final SimpMessagingTemplate messagingTemplate;
    private final DirectionWebSocketHandler directionWebSocketHandler;

    public DirectionService(SimpMessagingTemplate messagingTemplate,
                            DirectionWebSocketHandler directionWebSocketHandler) {
        this.messagingTemplate = messagingTemplate;
        this.directionWebSocketHandler = directionWebSocketHandler;
    }

    public void updateDirection(String direction) {
        this.currentDirection = direction;
        System.out.println("업데이트된 방향: " + direction);

        // 아두이노로 방향 정보 전송
        directionWebSocketHandler.sendDirection(direction);

        // 프론트엔드로 방향 정보 전송
        messagingTemplate.convertAndSend("/topic/direction", direction);
    }
}
