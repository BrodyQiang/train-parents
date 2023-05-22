package com.train.controller.web;

import com.train.bean.request.ConfirmOrderSaveReq;
import com.train.common.response.DBResult;
import com.train.service.ConfirmOrderService;
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
* @createDate 2023-05-16 18:54:37
*/

@RestController
@RequestMapping("/confirmOrder")
@Api(value = "ConfirmOrderController", tags = "")
public class ConfirmOrderWebController {

    @Autowired
    private ConfirmOrderService service;

    @PostMapping("/doConfirm")
    public DBResult doConfirm(@RequestBody @Valid ConfirmOrderSaveReq bean) {
        service.doConfirm(bean);
        return DBResult.success();
    }
}
