package com.train.controller;

import com.train.bean.request.TrainStationQueryReq;
import com.train.bean.request.TrainStationSaveReq;
import com.train.bean.response.TrainStationQueryRes;
import com.train.common.response.DBPages;
import com.train.common.response.DBResult;
import com.train.service.TrainStationService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
* @author Mr.Liu
* @email yxml2580@163.com
* @createDate 2023-05-05 18:35:01
*/

@RestController
@RequestMapping("/trainStation")
@Api(value = "TrainStationController", tags = "")
public class TrainStationController {

    @Autowired
    private TrainStationService service;

    @PostMapping("/save")
    public DBResult save(@RequestBody @Valid TrainStationSaveReq bean) {
        service.save(bean);
        return DBResult.success();
    }

    @GetMapping("/queryList")
    public DBPages<TrainStationQueryRes> queryList(@Valid TrainStationQueryReq bean) {
        return service.queryList(bean);
    }

    @DeleteMapping("/delete/{id}")
    public DBResult delete(@PathVariable Long id) {
        service.delete(id);
        return DBResult.success();
    }

}
