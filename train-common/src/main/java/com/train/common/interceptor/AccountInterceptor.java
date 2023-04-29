package com.train.common.interceptor;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.train.common.context.LoginAccountContext;
import com.train.common.response.AccountLoginRes;
import com.train.common.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 登录账号拦截器 用于获取登录账号信息
 */
@Component
public class AccountInterceptor implements HandlerInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger(AccountInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //获取header的token参数
        String accessToken = request.getHeader("accessToken");
        if (StrUtil.isNotBlank(accessToken)) {
            LOG.info("获取账号登录accessToken：{}", accessToken);
            JSONObject loginAccount = JwtUtil.getJSONObject(accessToken);
            LOG.info("当前登录账号：{}", loginAccount);
            AccountLoginRes member = JSONUtil.toBean(loginAccount, AccountLoginRes.class);
            LoginAccountContext.setAccount(member);
        }
        return true;
    }

}
