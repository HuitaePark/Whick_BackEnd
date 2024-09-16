package com.example.demo.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public class FaceDirectionController {
    @PostMapping("/face-direction")
    public String handleFaceDirection(@RequestBody FaceDirection direction) {
        // 아두이노로 전송할 명령을 생성
        String command = getCommand(direction);
        // 아두이노로 명령을 전송
        sendCommandToArduino(command);
        return "Command sent to Arduino";
    }

    private String getCommand(FaceDirection direction) {
        // 얼굴 방향에 따라 명령을 생성
        if (direction.getYaw() < 0) {
            return "left";
        } else if (direction.getYaw() > 0) {
            return "right";
        } else {
            return "stop";
        }
    }

    private void sendCommandToArduino(String command) {
        // 아두이노로 명령을 전송하는 로직을 구현
        // 예를 들어, WebSocket을 사용하여 아두이노로 명령을 전송
    }
}
