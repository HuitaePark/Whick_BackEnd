package com.dasom.whick.global.config;

import com.dasom.whick.domain.Direction.DirectionService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.listener.*;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

@Configuration
public class RedisConfig {

    @Bean
    public RedisMessageListenerContainer redisContainer(RedisConnectionFactory connectionFactory,
                                                        MessageListenerAdapter listenerAdapter) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(listenerAdapter, new PatternTopic("direction_channel"));
        return container;
    }

    @Bean
    public MessageListenerAdapter listenerAdapter(DirectionService directionService) {
        // DirectionService의 handleMessage 메서드를 메시지 리스너로 사용
        return new MessageListenerAdapter(directionService, "handleMessage");
    }
}