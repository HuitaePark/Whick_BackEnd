package com.dasom.whick.controller;

import com.dasom.whick.dto.PressureDto;
import com.dasom.whick.entity.Pressure;
import com.dasom.whick.repository.PressureRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
@RequestMapping("/pressure")
public class PressureController {

    private final PressureRepository pressureRepository;
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    private static final int PRESSURE_THRESHOLD = 28;  // 임계치 설정

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
                this.emitters.forEach(emitter -> {
                    try {
                        emitter.send(SseEmitter.event()
                                .name("pressure-event")
                                .data(pressureDto));
                    } catch (IOException e) {
                        emitters.remove(emitter);
                    }
                });
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
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitters.add(emitter);

        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError((e) -> emitters.remove(emitter));

        return emitter;
    }
}