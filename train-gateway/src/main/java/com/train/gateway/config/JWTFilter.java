package com.train.gateway.config;

import com.train.common.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@Order(-1)
public class JWTFilter implements GlobalFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String url = exchange.getRequest().getURI().getPath();
        System.out.println(url);
        // 1 判断URL
        if (url.toString().contains("login")) {
            System.out.println("无需登录，直接放行");
            return chain.filter(exchange);
        }
        // 2.获取请求参数
//        MultiValueMap<String, String> params = exchange.getRequest().getQueryParams();
//        // 3.获取authorization参数
//        String token = params.getFirst("authorization");

        String tokens = exchange.getRequest().getHeaders().getFirst("LOGIN_USER");

        // 4判断是否为空
        if (tokens != null && !"".equals(tokens)) {
            // 5 解析token
            Claims claims=null;
            try {
                claims = JwtUtil.parseJWT(tokens);
                String subject = claims.getSubject();
                System.out.println(subject);

            } catch (Exception e) {
                e.printStackTrace();
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
            }
            //6 判断解析是否成
            if (claims != null) {
                //7 成功了，放行
                return chain.filter(exchange);
            }
        }
        // 8.拦截
        // 8.1.禁止访问，设置状态码
        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
        // 8.2.结束处理
        return exchange.getResponse().setComplete();

    }
}
