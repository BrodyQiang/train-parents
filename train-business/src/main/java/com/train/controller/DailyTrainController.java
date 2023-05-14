package com.train.controller;

import com.train.bean.request.DailyTrainQueryReq;
import com.train.bean.request.DailyTrainSaveReq;
import com.train.bean.response.DailyTrainQueryRes;
import com.train.common.response.DBPages;
import com.train.common.response.DBResult;
import com.train.service.DailyTrainService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;

/**
* @author Mr.Liu
* @email yxml2580@163.com
* @createDate 2023-05-08 17:55:58
*/

@RestController
@RequestMapping("/admin/dailyTrain")
@Api(value = "DailyTrainController", tags = "")
public class DailyTrainController {

    @Autowired
    private DailyTrainService service;

    @PostMapping("/save")
    public DBResult save(@RequestBody @Valid DailyTrainSaveReq bean) {
        service.save(bean);
        return DBResult.success();
    }

    @GetMapping("/queryList")
    public DBPages<DailyTrainQueryRes> queryList(@Valid DailyTrainQueryReq bean) {
        return service.queryList(bean);
    }

    @DeleteMapping("/delete/{id}")
    public DBResult delete(@PathVariable Long id) {
        service.delete(id);
        return DBResult.success();
    }

    @GetMapping("/genDaily/{date}")
    public DBResult genDaily(@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
        service.genDaily(date);
        return DBResult.success();
    }

}
