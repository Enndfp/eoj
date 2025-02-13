package com.enndfp.eojbackendgateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * 网关全局拦截器，处理特定路径的权限控制
 *
 * @author Enndfp
 */
@Component
public class GlobalAuthFilter implements GlobalFilter, Ordered {

    // 使用 AntPathMatcher 来匹配路径模式
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    /**
     * 全局过滤器核心逻辑
     *
     * @param exchange 请求和响应的上下文
     * @param chain    过滤器链
     * @return Mono<Void> 返回 Mono 对象，表示异步响应
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest serverHttpRequest = exchange.getRequest();
        String path = serverHttpRequest.getURI().getPath();

        // 判断请求路径是否调用内部服务，如果是则返回 403 错误
        if (antPathMatcher.match("/**/inner/**", path)) {
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.FORBIDDEN); // 设置状态码为 403
            DataBufferFactory dataBufferFactory = response.bufferFactory();
            DataBuffer dataBuffer = dataBufferFactory.wrap("无权限".getBytes(StandardCharsets.UTF_8)); // 返回的错误信息

            // 通过 Mono 进行异步响应
            return response.writeWith(Mono.just(dataBuffer));
        }

        // 继续执行过滤器链
        return chain.filter(exchange);
    }

    /**
     * 设置过滤器的执行顺序。返回值越小，优先级越高。
     *
     * @return 过滤器优先级，0 表示最高优先级
     */
    @Override
    public int getOrder() {
        return 0; // 优先级最高，最先执行
    }
}
