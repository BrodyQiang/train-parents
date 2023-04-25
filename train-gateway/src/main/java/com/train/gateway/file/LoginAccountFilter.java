package com.train.gateway.file;

import com.train.gateway.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class LoginAccountFilter implements Ordered, GlobalFilter {

    private static final Logger LOG = LoggerFactory.getLogger(LoginAccountFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        // 排除不需要拦截的请求
        if (path.contains("/admin")
                || path.contains("/system/management/account/login")
                || path.contains("/system/management/account/rendCode")) {
            LOG.info("不需要登录验证：{}", path);
            return chain.filter(exchange);
        } else {
            LOG.info("需要登录验证：{}", path);
        }
        // 获取header的accessToken参数
        String accessToken = exchange.getRequest().getHeaders().getFirst("accessToken");
        LOG.info("账号登录验证开始，accessToken：{}", accessToken);
        if (accessToken == null || accessToken.isEmpty()) {
            LOG.info("accessToken为空，请求被拦截");
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            // 跳出过滤连
            return exchange.getResponse().setComplete();
        }

        // 校验accessToken是否有效，包括accessToken是否被改过，是否过期
        boolean validate = JwtUtil.validate(accessToken);
        if (validate) {
            LOG.info("accessToken有效，放行该请求");
            return chain.filter(exchange);
        } else {
            LOG.warn("accessToken无效，请求被拦截");
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

    }

    /**
     * 优先级设置  值越小  优先级越高
     *
     * @return
     */
    @Override
    public int getOrder() {
        return 0;
    }
}
