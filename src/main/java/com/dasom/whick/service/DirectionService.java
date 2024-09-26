package com.dasom.whick.service;

import lombok.Getter;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class DirectionService {
    @Getter
    private volatile String currentDirection = "unknown";
    private final SimpMessagingTemplate messagingTemplate;

    public DirectionService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void updateDirection(String direction) {
        this.currentDirection = direction;
        System.out.println("업데이트된 방향: " + direction);

        // 프론트엔드로 방향 정보 전송
        Map<String, String> directionMap = new HashMap<>();
        directionMap.put("direction", direction);
        messagingTemplate.convertAndSend("/topic/direction", directionMap);
    }
}
