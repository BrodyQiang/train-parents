package com.train.controller;

import com.train.bean.request.AccountPassengerQueryReq;
import com.train.bean.request.AccountPassengerSaveReq;
import com.train.bean.response.AccountPassengerQueryRes;
import com.train.common.context.LoginAccountContext;
import com.train.common.response.DBPages;
import com.train.common.response.DBResult;
import com.train.service.AccountPassengerService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

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
        service.save(bean);
        return DBResult.success();
    }

    @GetMapping("/queryList")
    public DBPages<AccountPassengerQueryRes> queryList(@Valid AccountPassengerQueryReq bean) {
        bean.setMemberId(LoginAccountContext.getId());
        return service.queryList(bean);
    }

    @DeleteMapping("/delete/{id}")
    public DBResult delete(@PathVariable Long id) {
        service.delete(id);
        return DBResult.success();
    }

    @GetMapping("/queryMine")
    public DBResult<List<AccountPassengerQueryRes>> queryMine() {
        return DBResult.success(service.queryMine());

    }
}
