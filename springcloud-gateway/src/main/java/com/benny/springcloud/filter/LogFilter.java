package com.benny.springcloud.filter;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * description
 *
 * @author Chenyujia
 * @date 2022/4/26 17:45
 * @since 1.0
 */
@Component
@Order(Ordered.LOWEST_PRECEDENCE)
@Slf4j
public class LogFilter implements GlobalFilter {

//    private static final Logger log = LoggerFactory.getLogger(LogFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        final ServerHttpRequest request = exchange.getRequest();
        final String path = request.getURI().getPath();
        long startTime = (Long) exchange.getAttributes().get("consumingStartTime");

        log.info("【请求：{}】耗时 {} ms", path, System.currentTimeMillis() - startTime);
        return chain.filter(exchange);
    }
}
