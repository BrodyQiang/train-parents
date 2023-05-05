package com.train.controller;

import com.train.bean.request.TrainCarriageQueryReq;
import com.train.bean.request.TrainCarriageSaveReq;
import com.train.bean.response.TrainCarriageQueryRes;
import com.train.common.response.DBPages;
import com.train.common.response.DBResult;
import com.train.service.TrainCarriageService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
* @author Mr.Liu
* @email yxml2580@163.com
* @createDate 2023-05-05 18:37:23
*/

@RestController
@RequestMapping("/trainCarriage")
@Api(value = "TrainCarriageController", tags = "")
public class TrainCarriageController {

    @Autowired
    private TrainCarriageService service;

    @PostMapping("/save")
    public DBResult save(@RequestBody @Valid TrainCarriageSaveReq bean) {
        service.save(bean);
        return DBResult.success();
    }

    @GetMapping("/queryList")
    public DBPages<TrainCarriageQueryRes> queryList(@Valid TrainCarriageQueryReq bean) {
        return service.queryList(bean);
    }

    @DeleteMapping("/delete/{id}")
    public DBResult delete(@PathVariable Long id) {
        service.delete(id);
        return DBResult.success();
    }

}
