package com.quotaGate.main_service.Config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quotaGate.main_service.DTO.RedisDTO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, RedisDTO> redisTemplate(
            RedisConnectionFactory connectionFactory) {

        RedisTemplate<String, RedisDTO> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();

        Jackson2JsonRedisSerializer<RedisDTO> serializer =
                new Jackson2JsonRedisSerializer<>(RedisDTO.class);
        serializer.setObjectMapper(mapper);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);

        template.afterPropertiesSet();

        return template;
    }
}