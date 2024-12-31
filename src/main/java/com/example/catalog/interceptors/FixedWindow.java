/*package com.example.catalog.interceptors;

import java.util.concurrent.ConcurrentHashMap;

public class FixedWindow {
    private final int maxRequests;
    private int requestCount;
    private long windowStart;

    public FixedWindow(int maxRequests) {
        this.maxRequests = maxRequests;
        this.requestCount = 0;
        this.windowStart = System.currentTimeMillis();
    }

    public boolean allowRequest() {
        synchronized (this) {
            long now = System.currentTimeMillis();

            // Reset the counter if the window has passed
            if (now - windowStart >= 60000) {
                requestCount = 0;
                windowStart = now;
            }

            if (requestCount < maxRequests) {
                requestCount++;
                return true;
            }

            return false;
        }
    }

    public int getRemainingRequests() {
        synchronized (this) {
            return Math.max(0, maxRequests - requestCount);
        }
    }

    public int getRetryAfterSeconds() {
        synchronized (this) {
            long now = System.currentTimeMillis();
            return (int) Math.max(0, (60000 - (now - windowStart)) / 1000);
        }
    }
}
*/