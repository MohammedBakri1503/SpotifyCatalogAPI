package com.example.catalog.interceptors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.Map;
import java.util.Queue;

@Component
public class RateLimit implements HandlerInterceptor {

    @Value("${rate-limit.algo}")
    private String rateLimitAlgo;

    @Value("${rate-limit.rpm}")
    private int rateLimitRPM;

    @Value("${rate-limit.enabled}")
    private boolean rateLimitEnabled;

    private static final Map<String, Queue<Long>> slidingWindowMap = new ConcurrentHashMap<>();
    private static final Map<String, Integer> fixedWindowMap = new ConcurrentHashMap<>();
    private static final Map<String, Long> windowStartMap = new ConcurrentHashMap<>();
    private static final long ONE_MINUTE_MILLIS = 60000;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!rateLimitEnabled || "/internal".equals(request.getRequestURI())) {
            return true;
        }

        String clientIp = request.getRemoteAddr();
        boolean allowed;

        if ("sliding".equalsIgnoreCase(rateLimitAlgo)) {
            allowed = isAllowedSliding(clientIp);
        } else {
            allowed = isAllowedFixed(clientIp);
        }

        if (!allowed) {
            response.setHeader("X-Rate-Limit-Remaining", "0");
            response.setHeader("X-Rate-Limit-Retry-After-Seconds", String.valueOf(getRetryAfter(clientIp)));
            response.setStatus(429);
            return false;
        }

        response.setHeader("X-Rate-Limit-Remaining", String.valueOf(getRemainingRequests(clientIp)));
        return true;
    }

    private boolean isAllowedSliding(String clientIp) {
        long now = System.currentTimeMillis();
        slidingWindowMap.putIfAbsent(clientIp, new ConcurrentLinkedDeque<>());

        Queue<Long> timestamps = slidingWindowMap.get(clientIp);

        synchronized (timestamps) {
            while (!timestamps.isEmpty() && (now - timestamps.peek() > ONE_MINUTE_MILLIS)) {
                timestamps.poll();
            }
            if (timestamps.size() < rateLimitRPM) {
                timestamps.add(now);
                return true;
            }
        }

        return false;
    }

    private boolean isAllowedFixed(String clientIp) {
        long now = System.currentTimeMillis();
        fixedWindowMap.putIfAbsent(clientIp, 0);
        windowStartMap.putIfAbsent(clientIp, now);

        synchronized (fixedWindowMap) {
            long windowStart = windowStartMap.get(clientIp);
            if (now - windowStart > ONE_MINUTE_MILLIS) {
                windowStartMap.put(clientIp, now);
                fixedWindowMap.put(clientIp, 0);
            }

            int requestCount = fixedWindowMap.get(clientIp);
            if (requestCount < rateLimitRPM) {
                fixedWindowMap.put(clientIp, requestCount + 1);
                return true;
            }
        }

        return false;
    }

    private int getRemainingRequests(String clientIp) {
        if ("sliding".equalsIgnoreCase(rateLimitAlgo)) {
            return rateLimitRPM - slidingWindowMap.getOrDefault(clientIp, new ConcurrentLinkedDeque<>()).size();
        } else {
            return rateLimitRPM - fixedWindowMap.getOrDefault(clientIp, 0);
        }
    }

    private long getRetryAfter(String clientIp) {
        if ("sliding".equalsIgnoreCase(rateLimitAlgo)) {
            long now = System.currentTimeMillis();
            Queue<Long> timestamps = slidingWindowMap.getOrDefault(clientIp, new ConcurrentLinkedDeque<>());
            if (timestamps.isEmpty()) {
                return 0;
            }
            return Math.max(0, ONE_MINUTE_MILLIS - (now - timestamps.peek())) / 1000;
        } else {
            long now = System.currentTimeMillis();
            long windowStart = windowStartMap.getOrDefault(clientIp, now);
            return Math.max(0, ONE_MINUTE_MILLIS - (now - windowStart)) / 1000;
        }
    }
}
