package com.dasom.whick.domain.Collision;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
@RequestMapping("/collision")
public class CollisionController {

    private final CollisionRepository collisionRepository;
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    // 위험 여부를 판단하는 임계치 예시 (필요에 따라 조정)
    private static final boolean RISK_THRESHOLD = true;

    public CollisionController(CollisionRepository collisionRepository) {
        this.collisionRepository = collisionRepository;
    }

    /**
     * 충돌 데이터를 수신하여 저장하고, 위험이 감지되면 SSE를 통해 알림을 보냅니다.
     */
    @PostMapping
    public ResponseEntity<String> receiveCollision(@RequestBody CollisionNotificationDto collisionDto) {
        // Collision 엔티티로 변환 후 저장
        Collision collision = new Collision();
        collision.setType(collisionDto.getType());

        Collision.CollisionData collisionData = new Collision.CollisionData();
        collisionData.setRisk(collisionDto.getData().isRisk());
        collision.setData(collisionData);

        collisionRepository.save(collision);

        // 위험이 감지되면 SSE를 통해 알림 전송
        if (collision.getData().isRisk() == RISK_THRESHOLD) {
            sendCollisionNotification(collisionDto);
        }

        return ResponseEntity.ok("Collision data received");
    }

    /**
     * 클라이언트가 SSE 연결을 요청하면 SseEmitter를 생성하여 리스트에 추가합니다.
     */
    @GetMapping("/sse")
    public SseEmitter streamCollisionData() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        this.emitters.add(emitter);

        emitter.onCompletion(() -> this.emitters.remove(emitter));
        emitter.onTimeout(() -> this.emitters.remove(emitter));
        emitter.onError(e -> this.emitters.remove(emitter));

        return emitter;
    }

    /**
     * 모든 연결된 클라이언트에게 충돌 알림을 전송합니다.
     */
    private void sendCollisionNotification(CollisionNotificationDto collisionDto) {
        List<SseEmitter> deadEmitters = new CopyOnWriteArrayList<>();

        this.emitters.forEach(emitter -> {
            try {
                emitter.send(SseEmitter.event()
                        .name("collision-event")
                        .data(collisionDto));
            } catch (IOException e) {
                deadEmitters.add(emitter);
            }
        });

        // 오류가 발생한 emitter는 리스트에서 제거
        this.emitters.removeAll(deadEmitters);
    }
}