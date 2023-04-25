package com.train.controller;

import com.train.common.response.DBResult;
import com.train.bean.request.AccountLoginReq;
import com.train.bean.request.AccountRegisterReq;
import com.train.bean.request.AccountSendCodeReq;
import com.train.bean.response.AccountLoginRes;
import com.train.service.AccountService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author Mr.Liu
 * @email yxml2580@163.com
 * @createDate 2023/4/20 19:55
 */
@RestController
@RequestMapping("/account")
@Api(value = "AccountController", tags = "账号管理")
public class AccountController {

    @Autowired
    private AccountService accountService;


    @GetMapping("/count")
    @ApiOperation(value = "统计账号数量", notes = "统计账号数量")
    public DBResult<Long> count() {
        return DBResult.success(accountService.count());
    }

    @PostMapping("/register")
    @ApiOperation(value = "注册账号", notes = "{\n" +
            " \"mobile\": \"12312321\"\n" +
            "}")
    public DBResult<Long> register(@RequestBody @Valid AccountRegisterReq bean) {
        return DBResult.success(accountService.register(bean));
    }

    @PostMapping("/rendCode")
    public DBResult rendCode(@RequestBody @Valid AccountSendCodeReq bean) {
        return DBResult.success(accountService.sendCode(bean));
    }

    @PostMapping("/login")
    public DBResult<AccountLoginRes> login(@RequestBody @Valid AccountLoginReq bean) {
        return DBResult.success(accountService.login(bean));
    }

}
