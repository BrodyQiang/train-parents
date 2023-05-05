package com.train.controller;

import com.train.bean.request.TrainSeatQueryReq;
import com.train.bean.request.TrainSeatSaveReq;
import com.train.bean.response.TrainSeatQueryRes;
import com.train.common.response.DBPages;
import com.train.common.response.DBResult;
import com.train.service.TrainSeatService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
* @author Mr.Liu
* @email yxml2580@163.com
* @createDate 2023-05-05 18:40:05
*/

@RestController
@RequestMapping("/admin/trainSeat")
@Api(value = "TrainSeatController", tags = "")
public class TrainSeatController {

    @Autowired
    private TrainSeatService service;

    @PostMapping("/save")
    public DBResult save(@RequestBody @Valid TrainSeatSaveReq bean) {
        service.save(bean);
        return DBResult.success();
    }

    @GetMapping("/queryList")
    public DBPages<TrainSeatQueryRes> queryList(@Valid TrainSeatQueryReq bean) {
        return service.queryList(bean);
    }

    @DeleteMapping("/delete/{id}")
    public DBResult delete(@PathVariable Long id) {
        service.delete(id);
        return DBResult.success();
    }

}
