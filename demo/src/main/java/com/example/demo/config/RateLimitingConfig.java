package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Basic Rate Limiting Configuration For production, consider using Redis or a
 * dedicated rate limiting service
 */
@Configuration
public class RateLimitingConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(rateLimitingInterceptor())
                .addPathPatterns("/api/auth/**") // Apply to authentication endpoints
                .addPathPatterns("/api/**"); // Apply to all API endpoints
    }

    @Bean
    public HandlerInterceptor rateLimitingInterceptor() {
        return new RateLimitingInterceptor();
    }

    /**
     * Simple in-memory rate limiting interceptor Production note: Use Redis or
     * distributed cache for scalability
     */
    public static class RateLimitingInterceptor implements HandlerInterceptor {

        private final ConcurrentHashMap<String, RequestCounter> requestCounts = new ConcurrentHashMap<>();
        private static final int MAX_REQUESTS_PER_MINUTE = 100; // Configurable
        private static final int AUTH_MAX_REQUESTS_PER_MINUTE = 10; // Stricter for auth endpoints

        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
            String clientIp = getClientIP(request);
            String requestPath = request.getRequestURI();

            // Different limits for different endpoints
            int maxRequests = isAuthEndpoint(requestPath) ? AUTH_MAX_REQUESTS_PER_MINUTE : MAX_REQUESTS_PER_MINUTE;

            RequestCounter counter = requestCounts.computeIfAbsent(clientIp, k -> new RequestCounter());

            if (counter.isLimitExceeded(maxRequests)) {
                response.setStatus(429); // Too Many Requests
                response.setHeader("X-RateLimit-Limit", String.valueOf(maxRequests));
                response.setHeader("X-RateLimit-Remaining", "0");
                response.setHeader("X-RateLimit-Reset", String.valueOf(counter.getResetTime()));
                response.getWriter().write("{\"error\":\"Rate limit exceeded. Too many requests.\"}");
                return false;
            }

            counter.increment();
            response.setHeader("X-RateLimit-Limit", String.valueOf(maxRequests));
            response.setHeader("X-RateLimit-Remaining", String.valueOf(Math.max(0, maxRequests - counter.getCount())));

            return true;
        }

        private String getClientIP(HttpServletRequest request) {
            String xfHeader = request.getHeader("X-Forwarded-For");
            if (xfHeader == null) {
                return request.getRemoteAddr();
            }
            return xfHeader.split(",")[0];
        }

        private boolean isAuthEndpoint(String path) {
            return path.contains("/auth/") || path.contains("/login") || path.contains("/register");
        }

        private static class RequestCounter {

            private final AtomicLong count = new AtomicLong(0);
            private LocalDateTime windowStart = LocalDateTime.now();

            public synchronized boolean isLimitExceeded(int maxRequests) {
                LocalDateTime now = LocalDateTime.now();
                if (ChronoUnit.MINUTES.between(windowStart, now) >= 1) {
                    // Reset window
                    count.set(0);
                    windowStart = now;
                }
                return count.get() >= maxRequests;
            }

            public long increment() {
                return count.incrementAndGet();
            }

            public long getCount() {
                return count.get();
            }

            public long getResetTime() {
                return windowStart.plus(1, ChronoUnit.MINUTES).toEpochSecond(java.time.ZoneOffset.UTC);
            }
        }
    }
}
