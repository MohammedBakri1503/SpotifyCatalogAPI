package com.example.catalog;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RateLimitITest {

    @Autowired
    private TestRestTemplate restTemplate;

    private static final String API_ENDPOINT = "/";
    private static final String INTERNAL_ENDPOINT = "/internal";
    private static final String XRateLimitRetryAfterSecondsHeader = "X-Rate-Limit-Retry-After-Seconds";
    private static final String XRateLimitRemaining = "X-Rate-Limit-Remaining";

    @Test
    public void testRateLimiterEnforcesLimits() throws InterruptedException {
        Thread.sleep(60000); // 1 minute

        int allowedRequests = 10;
        int extraRequests = 5;

        for (int i = 0; i < allowedRequests; i++) {
            ResponseEntity<String> response = restTemplate.getForEntity(API_ENDPOINT, String.class);
            assertTrue(response.getStatusCode().equals(HttpStatusCode.valueOf(200)), "Expected status code to be 200 for the first 10 requests");

            String remainingRequests = String.valueOf(allowedRequests - (i + 1));
            assertEquals(remainingRequests, response.getHeaders().get(XRateLimitRemaining).get(0), "Expected " + XRateLimitRemaining + " header to be " + remainingRequests + " after " + i + 1 + " requests");
        }

        for (int i = 0; i < extraRequests; i++) {
            ResponseEntity<String> response = restTemplate.getForEntity(API_ENDPOINT, String.class);
            assertTrue(response.getStatusCode().equals(HttpStatusCode.valueOf(429)));
            int retryAfter = Integer.parseInt(response.getHeaders().get(XRateLimitRetryAfterSecondsHeader).get(0));
            assertTrue(retryAfter > 0);
        }
    }

    @Test
    public void testRateLimiterBypassesInternalEndpoint() throws InterruptedException {
        Thread.sleep(60000); // 1 minute

        int totalRequests = 15;

        for (int i = 0; i < totalRequests; i++) {
            ResponseEntity<String> response = restTemplate.getForEntity(INTERNAL_ENDPOINT, String.class);
            assertTrue(response.getStatusCode().equals(HttpStatusCode.valueOf(200)));
            assertFalse(response.getHeaders().containsKey(XRateLimitRemaining));
        }
    }




    @Test
    public void testRequestsAfterRetryAfterInterval() throws InterruptedException {
        Thread.sleep(60000); // 1 minute

        int allowedRequests = 10;
        for (int i = 0; i < allowedRequests; i++) {
            ResponseEntity<String> response = restTemplate.getForEntity(API_ENDPOINT, String.class);
            assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        }

        // Wait for the retry-after interval to expire
        Thread.sleep(60000); // 1 minute

        ResponseEntity<String> response = restTemplate.getForEntity(API_ENDPOINT, String.class);
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode(), "Expected status code 200 after retry-after interval");
    }

    @Test
    public void testInvalidRequests() throws InterruptedException {
        Thread.sleep(60000); // 1 minute

        ResponseEntity<String> response = restTemplate.getForEntity("/invalid-endpoint", String.class);
        assertEquals(HttpStatusCode.valueOf(404), response.getStatusCode(), "Expected status code 404 for invalid endpoint");
    }

    /*@Test
    public void testHighLoadScenario() throws InterruptedException {
        Thread.sleep(60000); // 1 minute

        int clients = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(clients);
        AtomicInteger rateLimitedCount = new AtomicInteger();
        AtomicInteger successCount = new AtomicInteger();

        for (int i = 0; i < clients; i++) {
            executorService.execute(() -> {
                ResponseEntity<String> response = restTemplate.getForEntity(API_ENDPOINT, String.class);
                if (response.getStatusCode().equals(HttpStatusCode.valueOf(200))) {
                    successCount.incrementAndGet();
                } else if (response.getStatusCode().equals(HttpStatusCode.valueOf(429))) {
                    rateLimitedCount.incrementAndGet();
                }
            });
        }

        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.MINUTES);

        assertTrue(successCount.get() > 0, "Expected some successful requests");
        assertTrue(rateLimitedCount.get() > 0, "Expected some rate-limited requests");
    }*/




}