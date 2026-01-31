package com.fleetmanagement.fleetsystem.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private static final int MAX_REQUESTS_PER_MINUTE = 100;
    private final Map<String, RequestCounter> requestCounts = new ConcurrentHashMap<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String clientIp = getClientIP(request);

        RequestCounter counter = requestCounts.computeIfAbsent(clientIp, k -> new RequestCounter());

        if (counter.isExpired()) {
            counter.reset();
        }

        if (counter.increment() > MAX_REQUESTS_PER_MINUTE) {
            response.setStatus(429); // Too Many Requests
            response.getWriter().write("{\"error\": \"Rate limit exceeded. Maximum " + MAX_REQUESTS_PER_MINUTE + " requests per minute.\"}");
            response.setContentType("application/json");
            return false;
        }

        return true;
    }

    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }

    private static class RequestCounter {
        private final AtomicInteger count = new AtomicInteger(0);
        private long startTime = System.currentTimeMillis();

        public int increment() {
            return count.incrementAndGet();
        }

        public boolean isExpired() {
            return System.currentTimeMillis() - startTime > 60000; // 1 minute
        }

        public void reset() {
            count.set(0);
            startTime = System.currentTimeMillis();
        }
    }
}