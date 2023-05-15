package com.train.controller.web;

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
import java.util.List;

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

    @GetMapping("/queryAll")
    public DBResult<List<StationQueryRes>> queryAll() {
        return DBResult.success(service.queryAll());
    }

}
