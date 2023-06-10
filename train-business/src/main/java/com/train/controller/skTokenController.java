package com.train.controller;

import com.train.bean.request.SkTokenQueryReq;
import com.train.bean.request.SkTokenSaveReq;
import com.train.bean.response.SkTokenQueryRes;
import com.train.common.response.DBPages;
import com.train.common.response.DBResult;
import com.train.service.SkTokenService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
* @author Mr.Liu
* @email yxml2580@163.com
* @createDate 2023-06-10 17:41:31
*/

@RestController
@RequestMapping("/skToken")
@Api(value = "SkTokenController", tags = "")
public class SkTokenController {

    @Autowired
    private SkTokenService service;

    @PostMapping("/save")
    public DBResult save(@RequestBody @Valid SkTokenSaveReq bean) {
        service.save(bean);
        return DBResult.success();
    }

    @GetMapping("/queryList")
    public DBPages<SkTokenQueryRes> queryList(@Valid SkTokenQueryReq bean) {
        return service.queryList(bean);
    }

    @DeleteMapping("/delete/{id}")
    public DBResult delete(@PathVariable Long id) {
        service.delete(id);
        return DBResult.success();
    }

}
