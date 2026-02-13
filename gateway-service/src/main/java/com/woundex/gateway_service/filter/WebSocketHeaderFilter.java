package com.woundex.gateway_service.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Ensures WebSocket upgrade requests are properly forwarded.
 * Adds necessary headers for WebSocket proxying if missing.
 */
@Component
public class WebSocketHeaderFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String upgrade = request.getHeaders().getFirst(HttpHeaders.UPGRADE);

        if ("websocket".equalsIgnoreCase(upgrade)) {
            ServerHttpRequest mutated = request.mutate()
                    .header("X-Forwarded-Proto",
                            request.getURI().getScheme() != null ? request.getURI().getScheme() : "ws")
                    .header("X-Forwarded-Host",
                            request.getHeaders().getFirst(HttpHeaders.HOST) != null
                                    ? request.getHeaders().getFirst(HttpHeaders.HOST)
                                    : "localhost")
                    .build();

            return chain.filter(exchange.mutate().request(mutated).build());
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }
}
