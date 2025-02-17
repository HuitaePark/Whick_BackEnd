package com.dasom.whick.domain.Direction;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
public class DirectionController {

    private final DirectionService directionService;

    public DirectionController(DirectionService directionService) {
        this.directionService = directionService;
    }

    /**
     * 클라이언트가 SSE 연결을 요청할 때 호출
     *
     * @return SseEmitter 객체
     */
    @GetMapping("/direction/sse")
    public SseEmitter streamDirection() {
        return directionService.registerEmitter();
    }
}