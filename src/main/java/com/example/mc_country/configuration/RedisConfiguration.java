package com.example.mc_country.configuration;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Configuration
public class RedisConfiguration {
    @Value("${spring.data.redis.host}")
    String host;
    @Value("${spring.data.redis.port}")
    Integer port;

    @Bean
    public LettuceConnectionFactory lettuceConnectionFactory(){
        RedisStandaloneConfiguration configuration =
                new RedisStandaloneConfiguration(host, port);

        return new LettuceConnectionFactory(configuration);
    }

    @Bean
    public <F, S> RedisTemplate<F, S> redisTemplate(){
        final RedisTemplate<F, S> redisTemplate = new RedisTemplate<>();

        redisTemplate.setConnectionFactory(lettuceConnectionFactory());
        redisTemplate.setDefaultSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.afterPropertiesSet();

        return redisTemplate;
    }
}
