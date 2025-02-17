package com.dasom.whick.domain.Direction;

import com.dasom.whick.domain.Direction.Dto.DirectionEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final ObjectMapper objectMapper = new ObjectMapper(); // Jackson ObjectMapper 사용

    /**
     * SSE 클라이언트를 등록
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
     * Redis에서 수신한 메시지를 처리
     *
     * @param message Redis에서 수신한 메시지
     */
    public void handleMessage(String message) {
        try {
            // Redis에서 받은 메시지를 DirectionEvent 객체로 변환
            DirectionEvent event = objectMapper.readValue(message, DirectionEvent.class);

            // 현재 방향 업데이트
            this.currentDirection = event.getData().toString(); // 필요에 따라 수정

            // 프론트엔드로 방향 정보 전송
            for (SseEmitter emitter : emitters) {
                emitter.send(
                        SseEmitter.event()
                                .name("direction-event")
                                .data(event)
                );
            }

            System.out.println("업데이트된 방향: " + event);
        } catch (IOException e) {
            // 오류 발생 시 해당 emitter 제거
            emitters.removeIf(emitter -> {
                try {
                    emitter.send(
                            SseEmitter.event()
                                    .name("error")
                                    .data("연결 오류")
                    );
                    emitter.complete();
                } catch (IOException ex) {
                    return true;
                }
                return false;
            });
            e.printStackTrace();
        }
    }
}