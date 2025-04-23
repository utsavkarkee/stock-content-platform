package com.bunnywise.apigateway.filters;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.security.Key;
import java.util.Date;

@Component
public class AuthenticationFilter implements GlobalFilter {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);

    private static final String[] PUBLIC_PATHS = {
            "/actuator/health",
            "/auth/register",
            "/auth/login",
            "/actuator/health"
    };

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // Log headers
        logger.info("Incoming request: {} {}", request.getMethod(), request.getURI());
        request.getHeaders().forEach((key, value) -> logger.info("Header: {} = {}", key, value));

        // Allow public endpoints to bypass authentication
        for (String publicPath : PUBLIC_PATHS) {
            if (path.startsWith(publicPath)) {
                return chain.filter(exchange);
            }
        }

        if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
            logger.error("Missing Authorization header");
            throw new RuntimeException("Missing or invalid Authorization header");
        }

        // Extract token
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7); // Remove "Bearer "

        // Validate JWT
        if (!isValidToken(token)) {
            throw new RuntimeException("Invalid or expired JWT token");
        }

        return chain.filter(exchange);
    }

    public boolean isValidToken(String token) {
        try {
            // Decode the base64-encoded secret key
            byte[] keyBytes = java.util.Base64.getDecoder().decode(jwtSecret);
            Key key = Keys.hmacShaKeyFor(keyBytes);

            // Parse and validate the token
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            // Log claims for debugging
            logger.info("Token claims: {}", claims);

            // Check if the token is expired
            Date expiration = claims.getExpiration();
            boolean isExpired = expiration.before(new Date());
            if (isExpired) {
                logger.error("Token is expired. Expiration: {}", expiration);
                return false;
            }

            return true;
        } catch (ExpiredJwtException e) {
            logger.error("Token is expired: {}", e.getMessage());
            return false;
        } catch (UnsupportedJwtException e) {
            logger.error("Unsupported JWT: {}", e.getMessage());
            return false;
        } catch (MalformedJwtException e) {
            logger.error("Malformed JWT: {}", e.getMessage());
            return false;
        } catch (SignatureException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
            return false;
        } catch (IllegalArgumentException e) {
            logger.error("Invalid JWT: {}", e.getMessage());
            return false;
        }
    }
}
