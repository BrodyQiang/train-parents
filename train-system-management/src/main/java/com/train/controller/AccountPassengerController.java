package com.train.controller;

import com.train.bean.request.AccountPassengerSaveReq;
import com.train.common.response.DBResult;
import com.train.service.AccountPassengerService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @author Mr.Liu
 * @email yxml2580@163.com
 * @createDate 2023/4/20 19:55
 */
@RestController
@RequestMapping("/accountPassenger")
@Api(value = "AccountPassengerController", tags = "乘车人账号相关接口")
public class AccountPassengerController {

    @Autowired
    private AccountPassengerService service;

    @PostMapping("/save")
    public DBResult save(@RequestBody @Valid AccountPassengerSaveReq bean) {
        return service.save(bean);
    }

}
