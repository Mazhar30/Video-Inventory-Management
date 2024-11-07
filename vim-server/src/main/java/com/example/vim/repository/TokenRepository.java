package com.example.vim.repository;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class TokenRepository {

    private final StringRedisTemplate redisTemplate;

    public TokenRepository(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void saveToken(String username, String token) {
        redisTemplate.opsForValue().set(username, token);
    }

    public String getToken(String username) {
        return redisTemplate.opsForValue().get(username);
    }

    public void deleteToken(String username) {
        redisTemplate.delete(username);
    }
}
