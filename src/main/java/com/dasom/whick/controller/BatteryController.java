package com.dasom.whick.controller;

import com.dasom.whick.dto.BatteryDto;
import com.dasom.whick.entity.Battery;
import com.dasom.whick.repository.BatteryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
@RequestMapping("/battery")
public class BatteryController {

    private final BatteryRepository batteryRepository;
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    private static final int BATTERY_THRESHOLD = 20;  // 임계치 설정

    public BatteryController(BatteryRepository batteryRepository) {
        this.batteryRepository = batteryRepository;
    }

    @PostMapping
    public ResponseEntity<String> receiveBattery(@Valid @RequestBody BatteryDto batteryDto, BindingResult bindingResult) {
        // 유효성 검증 실패 시 처리
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors().get(0).getDefaultMessage();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
        }

        // Battery 모델로 변환 후 저장
        Battery battery = new Battery();
        battery.setLevel(batteryDto.getLevel());
        battery.setStatus(batteryDto.getStatus());
        batteryRepository.save(battery);

        // 임계치 이하일 경우 SSE를 통해 프론트엔드로 알림 전송
        Integer batteryLevel = battery.getLevel();
        if (batteryLevel != null && batteryLevel < BATTERY_THRESHOLD) {
            // 이벤트 이름을 지정하여 클라이언트가 특정 이벤트를 구독할 수 있도록 함
            emitters.forEach(emitter -> {
                try {
                    emitter.send(SseEmitter.event()
                            .name("battery-event")
                            .data(batteryDto));
                } catch (IOException e) {
                    emitters.remove(emitter);
                    // 로그를 남기거나 추가적인 에러 처리를 할 수 있습니다.
                    System.err.println("Failed to send SSE notification: " + e.getMessage());
                }
            });
        }

        return ResponseEntity.ok("Battery data received");
    }

    @GetMapping("/sse")
    public SseEmitter streamBatteryData() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitters.add(emitter);

        // 클라이언트 연결이 완료되거나 타임아웃, 오류가 발생하면 emitters 리스트에서 제거
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError((e) -> {
            emitters.remove(emitter);
            // 로그를 남기거나 추가적인 에러 처리를 할 수 있습니다.
            System.err.println("SseEmitter error: " + e.getMessage());
        });

        return emitter;
    }
}