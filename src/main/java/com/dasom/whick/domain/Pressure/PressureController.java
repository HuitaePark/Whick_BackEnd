package com.dasom.whick.domain.Pressure;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import com.fasterxml.jackson.databind.ObjectMapper; // JSON 변환을 위한 라이브러리 추가

import java.io.IOException;
import java.util.*;
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
    public ResponseEntity<String> receivePressure(@RequestBody Pressure pressure) {
        try {
            // Pressure 엔티티 저장
            pressureRepository.save(pressure);

            // 임계치 이하일 경우 SSE를 통해 프론트엔드로 알림 전송
            if (pressure.getPressure() < PRESSURE_THRESHOLD) {
                // 전송할 JSON 데이터 생성
                String jsonData = createJsonData("Air Pressure", true);

                this.emitters.forEach(emitter -> {
                    try {
                        emitter.send(SseEmitter.event()
                                .name("pressure-event")
                                .data(jsonData, MediaType.APPLICATION_JSON));
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