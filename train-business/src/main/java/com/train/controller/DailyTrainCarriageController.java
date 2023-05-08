package com.train.controller;

import com.train.bean.request.DailyTrainCarriageQueryReq;
import com.train.bean.request.DailyTrainCarriageSaveReq;
import com.train.bean.response.DailyTrainCarriageQueryRes;
import com.train.common.response.DBPages;
import com.train.common.response.DBResult;
import com.train.service.DailyTrainCarriageService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
* @author Mr.Liu
* @email yxml2580@163.com
* @createDate 2023-05-08 18:11:12
*/

@RestController
@RequestMapping("/dailyTrainCarriage")
@Api(value = "DailyTrainCarriageController", tags = "")
public class DailyTrainCarriageController {

    @Autowired
    private DailyTrainCarriageService service;

    @PostMapping("/save")
    public DBResult save(@RequestBody @Valid DailyTrainCarriageSaveReq bean) {
        service.save(bean);
        return DBResult.success();
    }

    @GetMapping("/queryList")
    public DBPages<DailyTrainCarriageQueryRes> queryList(@Valid DailyTrainCarriageQueryReq bean) {
        return service.queryList(bean);
    }

    @DeleteMapping("/delete/{id}")
    public DBResult delete(@PathVariable Long id) {
        service.delete(id);
        return DBResult.success();
    }

}
