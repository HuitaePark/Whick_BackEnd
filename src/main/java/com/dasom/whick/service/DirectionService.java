package com.dasom.whick.service;

import com.dasom.whick.dto.CollisionNotificationDto; // 필요한 경우
import lombok.Getter;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class DirectionService {
    @Getter
    private volatile String currentDirection = "unknown";
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    public DirectionService() {
        // 기본 생성자
    }

    /**
     * SSE 클라이언트를 등록합니다.
     *
     * @return SseEmitter 객체
     */
    public SseEmitter registerEmitter() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        this.emitters.add(emitter);

        emitter.onCompletion(() -> this.emitters.remove(emitter));
        emitter.onTimeout(() -> this.emitters.remove(emitter));
        emitter.onError(e -> this.emitters.remove(emitter));

        return emitter;
    }

    /**
     * 방향을 업데이트하고 모든 클라이언트에게 SSE로 전송합니다.
     *
     * @param direction 업데이트할 방향
     */
    public void updateDirection(String direction) {
        this.currentDirection = direction;
        System.out.println("업데이트된 방향: " + direction);

        // 프론트엔드로 방향 정보 전송
        try {
            // 데이터 포맷을 필요에 맞게 조정할 수 있습니다.
            String data = "{\"direction\":\"" + direction + "\"}";
            for (SseEmitter emitter : emitters) {
                emitter.send(SseEmitter.event()
                        .name("direction-event")
                        .data(data));
            }
        } catch (IOException e) {
            // 오류 발생 시 해당 emitter 제거
            emitters.removeIf(emitter -> {
                try {
                    emitter.send(SseEmitter.event().name("error").data("연결 오류"));
                    emitter.complete();
                } catch (IOException ex) {
                    return true;
                }
                return false;
            });
        }
    }
}