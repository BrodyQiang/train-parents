package com.train.controller;

import com.train.bean.request.TrainQueryReq;
import com.train.bean.request.TrainSaveReq;
import com.train.bean.response.TrainQueryRes;
import com.train.common.response.DBPages;
import com.train.common.response.DBResult;
import com.train.service.TrainSeatService;
import com.train.service.TrainService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
* @author Mr.Liu
* @email yxml2580@163.com
* @createDate 2023-05-05 18:32:50
*/

@RestController
@RequestMapping("/admin/train")
@Api(value = "TrainController", tags = "")
public class TrainController {

    @Autowired
    private TrainService service;

    @Autowired
    private TrainSeatService trainSeatService;

    @PostMapping("/save")
    public DBResult save(@RequestBody @Valid TrainSaveReq bean) {
        service.save(bean);
        return DBResult.success();
    }

    @GetMapping("/queryList")
    public DBPages<TrainQueryRes> queryList(@Valid TrainQueryReq bean) {
        return service.queryList(bean);
    }

    @DeleteMapping("/delete/{id}")
    public DBResult delete(@PathVariable Long id) {
        service.delete(id);
        return DBResult.success();
    }


    @GetMapping("/genSeat/{trainCode}")
    public DBResult<Object> genSeat(@PathVariable String trainCode) {
        trainSeatService.genTrainSeat(trainCode);
        return DBResult.success();
    }

}
