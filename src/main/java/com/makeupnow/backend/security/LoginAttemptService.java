package com.makeupnow.backend.security;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class LoginAttemptService {

    private final int MAX_ATTEMPT = 5;
    private final long BLOCK_TIME = 15 * 60 * 1000; // 15 minutes

    private final Map<String, Integer> attemptsCache = new ConcurrentHashMap<>();
    private final Map<String, Long> blockedUntil = new ConcurrentHashMap<>();

    public void loginFailed(String key) {
        int attempts = attemptsCache.getOrDefault(key, 0);
        attempts++;
        attemptsCache.put(key, attempts);
        if (attempts >= MAX_ATTEMPT) {
            blockedUntil.put(key, System.currentTimeMillis() + BLOCK_TIME);
        }
    }

    public void loginSucceeded(String key) {
        attemptsCache.remove(key);
        blockedUntil.remove(key);
    }

    public boolean isBlocked(String key) {
        Long until = blockedUntil.get(key);
        if (until == null) return false;
        if (System.currentTimeMillis() > until) {
            blockedUntil.remove(key);
            attemptsCache.remove(key);
            return false;
        }
        return true;
    }
}
