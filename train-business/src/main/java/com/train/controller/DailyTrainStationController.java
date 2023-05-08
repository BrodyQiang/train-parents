package com.train.controller;

import com.train.bean.request.DailyTrainStationQueryReq;
import com.train.bean.request.DailyTrainStationSaveReq;
import com.train.bean.response.DailyTrainStationQueryRes;
import com.train.common.response.DBPages;
import com.train.common.response.DBResult;
import com.train.service.DailyTrainStationService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
* @author Mr.Liu
* @email yxml2580@163.com
* @createDate 2023-05-08 18:04:19
*/

@RestController
@RequestMapping("/admin/dailyTrainStation")
@Api(value = "DailyTrainStationController", tags = "")
public class DailyTrainStationController {

    @Autowired
    private DailyTrainStationService service;

    @PostMapping("/save")
    public DBResult save(@RequestBody @Valid DailyTrainStationSaveReq bean) {
        service.save(bean);
        return DBResult.success();
    }

    @GetMapping("/queryList")
    public DBPages<DailyTrainStationQueryRes> queryList(@Valid DailyTrainStationQueryReq bean) {
        return service.queryList(bean);
    }

    @DeleteMapping("/delete/{id}")
    public DBResult delete(@PathVariable Long id) {
        service.delete(id);
        return DBResult.success();
    }

}
