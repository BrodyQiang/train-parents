package com.train.common.context;

import com.train.common.response.AccountLoginRes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginAccountContext {
    private static final Logger LOG = LoggerFactory.getLogger(LoginAccountContext.class);

    private static ThreadLocal<AccountLoginRes> account = new ThreadLocal<>();

    public static AccountLoginRes getAccount() {
        return account.get();
    }

    public static void setAccount(AccountLoginRes member) {
        LoginAccountContext.account.set(member);
    }


    public static Long getId() {
        try {
            return account.get().getId();
        } catch (Exception e) {
            LOG.error("获取登录账号信息异常", e);
            throw e;
        }
    }

}
