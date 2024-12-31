/*
package com.example.catalog.interceptors;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;


public class SlidingWindow {
    private final int maxRequests;
    private final long[] timestamps;
    private int index;

    public SlidingWindow(int maxRequests) {
        this.maxRequests = maxRequests;
        this.timestamps = new long[maxRequests];
        this.index = 0;
    }

    public boolean allowRequest() {
        synchronized (this) {
            long now = System.currentTimeMillis();
            long oldestTimestamp = timestamps[index % maxRequests];

            // Deny if within the last 60 seconds
            if (now - oldestTimestamp < 60000 && oldestTimestamp != 0) {
                return false;
            }

            // Update the current timestamp
            timestamps[index % maxRequests] = now;
            index++;
            return true;
        }
    }

    public int getRemainingRequests() {
        synchronized (this) {
            long now = System.currentTimeMillis();
            int count = 0;

            for (long timestamp : timestamps) {
                if (now - timestamp < 60000 && timestamp != 0) {
                    count++;
                }
            }

            return maxRequests - count;
        }
    }

    public int getRetryAfterSeconds() {
        synchronized (this) {
            long now = System.currentTimeMillis();
            long nextAllowedTime = Long.MAX_VALUE;

            for (long timestamp : timestamps) {
                if (timestamp != 0) {
                    nextAllowedTime = Math.min(nextAllowedTime, timestamp + 60000);
                }
            }

            return (int) Math.max(0, (nextAllowedTime - now) / 1000);
        }
    }
}
*/