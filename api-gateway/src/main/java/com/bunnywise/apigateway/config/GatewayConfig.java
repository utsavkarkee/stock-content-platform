package com.bunnywise.apigateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth-service", r -> r.path("/auth/**")
                        .uri(ServiceConstants.AUTH_SERVICE))
                .route("content-service", r -> r.path("/content/**")
                        .uri(ServiceConstants.CONTENT_SERVICE))
                .route("subscription-service", r -> r.path("/subscription/**")
                        .uri(ServiceConstants.SUBSCRIPTION_SERVICE))
                .route("storage-service", r -> r.path("/storage/**")
                        .uri(ServiceConstants.STORAGE_SERVICE))
                .build();
    }

}

