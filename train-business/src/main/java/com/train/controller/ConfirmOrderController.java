package com.train.controller;

import com.train.bean.request.ConfirmOrderQueryReq;
import com.train.bean.request.ConfirmOrderSaveReq;
import com.train.bean.response.ConfirmOrderQueryRes;
import com.train.common.response.DBPages;
import com.train.common.response.DBResult;
import com.train.service.ConfirmOrderService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
* @author Mr.Liu
* @email yxml2580@163.com
* @createDate 2023-05-16 18:54:37
*/

@RestController
@RequestMapping("/admin/confirmOrder")
@Api(value = "ConfirmOrderController", tags = "")
public class ConfirmOrderController {

    @Autowired
    private ConfirmOrderService service;

    @PostMapping("/save")
    public DBResult save(@RequestBody @Valid ConfirmOrderSaveReq bean) {
        service.save(bean);
        return DBResult.success();
    }

    @GetMapping("/queryList")
    public DBPages<ConfirmOrderQueryRes> queryList(@Valid ConfirmOrderQueryReq bean) {
        return service.queryList(bean);
    }

    @DeleteMapping("/delete/{id}")
    public DBResult delete(@PathVariable Long id) {
        service.delete(id);
        return DBResult.success();
    }

}
