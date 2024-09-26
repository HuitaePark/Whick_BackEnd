package com.dasom.whick.controller;

import com.dasom.whick.dto.BatteryDto;
import com.dasom.whick.model.Battery;
import com.dasom.whick.repository.BatteryRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@RestController
@RequestMapping("/api/battery")
public class BatteryController {

    private final BatteryRepository batteryRepository;
    private SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

    private static final int BATTERY_THRESHOLD = 20;  // 임계치 설정

    public BatteryController(BatteryRepository batteryRepository) {
        this.batteryRepository = batteryRepository;
    }

    @PostMapping
    public ResponseEntity<String> receiveBattery(@RequestBody BatteryDto batteryDto) {
        // Battery 모델로 변환 후 저장
        Battery battery = new Battery();
        battery.setLevel(batteryDto.getLevel());
        battery.setStatus(batteryDto.getStatus());
        batteryRepository.save(battery);

        // 임계치 이하일 경우 SSE를 통해 프론트엔드로 알림 전송
        if (battery.getLevel() < BATTERY_THRESHOLD) {
            try {
                emitter.send(batteryDto);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return ResponseEntity.ok("Battery data received");
    }

    @GetMapping("/sse")
    public SseEmitter streamBatteryData() {
        return emitter;  // 프론트엔드에 SSE 연결
    }
}