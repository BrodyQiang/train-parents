package com.train.controller;

import com.train.bean.request.skTokenQueryReq;
import com.train.bean.request.skTokenSaveReq;
import com.train.bean.response.skTokenQueryRes;
import com.train.common.response.DBPages;
import com.train.common.response.DBResult;
import com.train.service.skTokenService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
* @author Mr.Liu
* @email yxml2580@163.com
* @createDate 2023-06-10 17:31:43
*/

@RestController
@RequestMapping("/skToken")
@Api(value = "skTokenController", tags = "")
public class skTokenController {

    @Autowired
    private skTokenService service;

    @PostMapping("/save")
    public DBResult save(@RequestBody @Valid skTokenSaveReq bean) {
        service.save(bean);
        return DBResult.success();
    }

    @GetMapping("/queryList")
    public DBPages<skTokenQueryRes> queryList(@Valid skTokenQueryReq bean) {
        return service.queryList(bean);
    }

    @DeleteMapping("/delete/{id}")
    public DBResult delete(@PathVariable Long id) {
        service.delete(id);
        return DBResult.success();
    }

}
