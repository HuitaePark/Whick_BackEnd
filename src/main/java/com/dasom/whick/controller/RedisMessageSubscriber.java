package com.dasom.whick.controller;

import com.dasom.whick.service.DirectionService;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;


@Component
public class RedisMessageSubscriber implements MessageListener {

    private final DirectionService directionService;

    public RedisMessageSubscriber(DirectionService directionService) {
        this.directionService = directionService;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String direction = new String(message.getBody());
        directionService.updateDirection(direction);
    }
}