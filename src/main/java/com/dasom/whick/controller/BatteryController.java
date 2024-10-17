package com.dasom.whick.controller;

import com.dasom.whick.entity.Battery;
import com.dasom.whick.repository.BatteryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import com.fasterxml.jackson.databind.ObjectMapper; // JSON 변환을 위한 라이브러리 추가

import jakarta.validation.Valid;
import java.io.IOException;
import java.util.*;
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
    public ResponseEntity<String> receiveBattery(@Valid @RequestBody Battery battery, BindingResult bindingResult) {
        // 유효성 검증 실패 시 처리
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors().get(0).getDefaultMessage();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
        }

        // Battery 엔티티 저장
        batteryRepository.save(battery);

        // 임계치 이하일 경우 SSE를 통해 프론트엔드로 알림 전송
        Integer batteryLevel = battery.getLevel();
        if (batteryLevel != null && batteryLevel < BATTERY_THRESHOLD) {
            // 전송할 JSON 데이터 생성
            String jsonData = createJsonData("Charge", true);

            // SSE를 통해 JSON 데이터 전송
            emitters.forEach(emitter -> {
                try {
                    emitter.send(SseEmitter.event()
                            .name("battery-event")
                            .data(jsonData, MediaType.APPLICATION_JSON));
                } catch (IOException e) {
                    emitters.remove(emitter);
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
            System.err.println("SseEmitter error: " + e.getMessage());
        });

        return emitter;
    }

    // JSON 데이터를 생성하는 헬퍼 메서드
    private String createJsonData(String type, boolean insufficient) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> jsonMap = new HashMap<>();
            jsonMap.put("type", type);

            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("insufficient", insufficient);

            jsonMap.put("data", dataMap);

            return objectMapper.writeValueAsString(jsonMap);
        } catch (Exception e) {
            e.printStackTrace();
            return "{}";
        }
    }
}