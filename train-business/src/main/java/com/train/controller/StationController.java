package com.train.controller;

import com.train.bean.request.StationQueryReq;
import com.train.bean.request.StationSaveReq;
import com.train.bean.response.StationQueryRes;
import com.train.common.response.DBPages;
import com.train.common.response.DBResult;
import com.train.service.StationService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
* @author Mr.Liu
* @email yxml2580@163.com
* @createDate 2023-05-04 19:58:40
*/

@RestController
@RequestMapping("/station")
@Api(value = "StationController", tags = "")
public class StationController {

    @Autowired
    private StationService service;

    @PostMapping("/save")
    public DBResult save(@RequestBody @Valid StationSaveReq bean) {
        service.save(bean);
        return DBResult.success();
    }

    @GetMapping("/queryList")
    public DBPages<StationQueryRes> queryList(@Valid StationQueryReq bean) {
        return service.queryList(bean);
    }

    @DeleteMapping("/delete/{id}")
    public DBResult delete(@PathVariable Long id) {
        service.delete(id);
        return DBResult.success();
    }

}
