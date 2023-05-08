package com.train.controller;

import com.train.bean.request.DailyTrainSeatQueryReq;
import com.train.bean.request.DailyTrainSeatSaveReq;
import com.train.bean.response.DailyTrainSeatQueryRes;
import com.train.common.response.DBPages;
import com.train.common.response.DBResult;
import com.train.service.DailyTrainSeatService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
* @author Mr.Liu
* @email yxml2580@163.com
* @createDate 2023-05-08 18:23:36
*/

@RestController
@RequestMapping("/admin/dailyTrainSeat")
@Api(value = "DailyTrainSeatController", tags = "")
public class DailyTrainSeatController {

    @Autowired
    private DailyTrainSeatService service;

    @PostMapping("/save")
    public DBResult save(@RequestBody @Valid DailyTrainSeatSaveReq bean) {
        service.save(bean);
        return DBResult.success();
    }

    @GetMapping("/queryList")
    public DBPages<DailyTrainSeatQueryRes> queryList(@Valid DailyTrainSeatQueryReq bean) {
        return service.queryList(bean);
    }

    @DeleteMapping("/delete/{id}")
    public DBResult delete(@PathVariable Long id) {
        service.delete(id);
        return DBResult.success();
    }

}
