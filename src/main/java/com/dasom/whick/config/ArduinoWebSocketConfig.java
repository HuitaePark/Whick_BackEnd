package com.dasom.whick.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;
import com.dasom.whick.handler.DirectionWebSocketHandler;

@Configuration
@EnableWebSocket
public class ArduinoWebSocketConfig implements WebSocketConfigurer {

    private final DirectionWebSocketHandler directionWebSocketHandler;

    public ArduinoWebSocketConfig(DirectionWebSocketHandler directionWebSocketHandler) {
        this.directionWebSocketHandler = directionWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(directionWebSocketHandler, "/arduino-direction")
                .setAllowedOrigins("*");
    }
}