package com.dasom.whick.controller;

import com.dasom.whick.dto.PressureDto;
import com.dasom.whick.entity.Pressure;
import com.dasom.whick.repository.PressureRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@RestController
@RequestMapping("/pressure")
public class PressureController {

    private final PressureRepository pressureRepository;
    private final SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

    private static final float PRESSURE_THRESHOLD = 28.0f;  // 임계치 설정

    public PressureController(PressureRepository pressureRepository) {
        this.pressureRepository = pressureRepository;
    }

    @PostMapping
    public ResponseEntity<String> receivePressure(@RequestBody PressureDto pressureDto) {
        try {
            // Pressure 모델로 변환 후 저장
            Pressure pressure = new Pressure();
            pressure.setPressure(pressureDto.getPressure());
            pressure.setStatus(pressureDto.getStatus());
            pressureRepository.save(pressure);

            // 임계치 이하일 경우 SSE를 통해 프론트엔드로 알림 전송
            if (pressure.getPressure() < PRESSURE_THRESHOLD) {
                try {
                    emitter.send(pressureDto);
                } catch (IOException e) {
                    e.printStackTrace();
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Failed to send SSE notification");
                }
            }

            return ResponseEntity.ok("Pressure data received");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while processing the request");
        }
    }

    @GetMapping("/sse")
    public SseEmitter streamPressureData() {
        return emitter;  // 프론트엔드에 SSE 연결
    }
}