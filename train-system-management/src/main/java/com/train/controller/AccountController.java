package com.train.controller;

import com.train.common.response.DBResult;
import com.train.request.AccountLoginReq;
import com.train.request.AccountRegisterReq;
import com.train.request.AccountSendCodeReq;
import com.train.response.AccountLoginRes;
import com.train.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @author Mr.Liu
 * @email yxml2580@163.com
 * @createDate 2023/4/20 19:55
 */
@RestController
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @GetMapping("/count")
    public DBResult<Long> count() {
        return DBResult.success(accountService.count());
    }

    @PostMapping("/register")
    public DBResult<Long> register(@Valid AccountRegisterReq bean) {
        return DBResult.success(accountService.register(bean));
    }

    @PostMapping("/rendCode")
    public DBResult rendCode(@Valid AccountSendCodeReq bean) {
        accountService.sendCode(bean);
        return DBResult.success();
    }

    @PostMapping("/login")
    public DBResult<AccountLoginRes> login(@Valid AccountLoginReq bean) {
        return DBResult.success(accountService.login(bean));
    }

}
