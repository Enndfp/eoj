package com.enndfp.eojbackendgateway.filter;

import com.enndfp.eojbackendcommon.utils.JwtUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JWT 统一鉴权全局过滤器
 * <p>
 * 该过滤器用于拦截所有请求，校验请求头中的 JWT Token 是否有效。
 * 对于需要鉴权的请求，若 Token 无效则返回 401 未授权响应。
 * 对于不需要鉴权的请求，直接放行。
 *
 * @author Enndfp
 */
@Component
public class AuthFilter implements GlobalFilter, Ordered {

    /**
     * 配置文件中的参数：不需要校验的链接列表
     * Spring Boot 会将用逗号分隔的字符串自动转为 List<String> 类型
     */
    @Value("#{'${gateway.excludedUrls}'.split(',')}")
    private List<String> excludedUrls;

    /**
     * 核心过滤逻辑
     *
     * @param exchange 包含请求和响应信息的对象
     * @param chain    过滤器链
     * @return Mono<Void> 返回响应流
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1. 获取当前请求的路径
        String path = exchange.getRequest().getURI().getPath();

        // 如果当前路径在排除列表中，则跳过鉴权，直接放行
        if (excludedUrls.contains(path)) {
            return chain.filter(exchange);
        }

        // 2. 获取请求头中的 Authorization token
        String token = exchange.getRequest().getHeaders().getFirst("Authorization");

        // 如果 token 不为空，且带有 Bearer 前缀，去掉前缀部分
        if (!StringUtils.isEmpty(token)) {
            token = token.replace("Bearer ", "");
        }

        ServerHttpResponse response = exchange.getResponse();

        // 3. 校验 token 是否有效
        boolean verifyToken = JwtUtil.verifyToken(token);

        // 如果 token 无效，返回 401 未授权错误
        if (!verifyToken) {
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("errCode", 401);
            responseData.put("errMessage", "用户未登录");
            return responseError(response, responseData);
        }

        // Token 校验通过，继续执行下一个过滤器
        return chain.filter(exchange);
    }

    /**
     * 将错误信息返回客户端
     *
     * @param response     响应对象
     * @param responseData 错误数据
     * @return Mono<Void> 返回响应流
     */
    private Mono<Void> responseError(ServerHttpResponse response, Map<String, Object> responseData) {
        // 使用 Jackson 将 Map 转换为 JSON 字符串
        ObjectMapper objectMapper = new ObjectMapper();
        byte[] data = new byte[0];
        try {
            data = objectMapper.writeValueAsBytes(responseData);
        } catch (JsonProcessingException e) {
            e.printStackTrace(); // 如果转换失败，记录异常
        }

        // 创建 DataBuffer，包装响应数据
        DataBuffer buffer = response.bufferFactory().wrap(data);

        // 设置响应状态为 401 Unauthorized，并添加 Content-Type 头
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");

        // 返回响应流
        return response.writeWith(Mono.just(buffer));
    }

    /**
     * 配置过滤器执行顺序
     *
     * @return 执行顺序的优先级
     */
    @Override
    public int getOrder() {
        // 设置过滤器执行顺序，数字越大，优先级越低
        return Ordered.LOWEST_PRECEDENCE; // 最后执行
    }
}
