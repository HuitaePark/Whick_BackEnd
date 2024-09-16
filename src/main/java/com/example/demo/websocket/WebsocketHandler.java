package com.example.demo.websocket;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class WebSocketHandler extends TextWebSocketHandler {
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 클라이언트로부터 받은 메시지를 처리합니다.
        // 여기서는 파이썬 스크립트를 호출하여 얼굴인식을 수행합니다.
        String result = callPythonScript(message.getText());
        session.sendMessage(new TextMessage(result));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // 연결이 종료되면 수행할 작업을 정의합니다.
    }

    private String callPythonScript(String imagePath) throws IOException {
        // 파이썬 스크립트를 호출하여 얼굴인식을 수행합니다.
        // ProcessBuilder 또는 다른 방법을 사용하여 파이썬 스크립트를 실행할 수 있습니다.
        // 여기서는 간단한 예시로 ProcessBuilder를 사용합니다.
        ProcessBuilder processBuilder = new ProcessBuilder("python", "face_angle.py", imagePath);
        Process process = processBuilder.start();
        // 프로세스의 출력을 읽어 결과를 반환합니다.
        // ...
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        StringBuilder result = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            result.append(line);
        }
        return result.toString();

    }
}
