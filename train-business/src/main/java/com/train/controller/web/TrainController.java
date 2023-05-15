package com.train.controller.web;

import com.train.bean.response.TrainQueryRes;
import com.train.common.response.DBResult;
import com.train.service.TrainService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    @GetMapping("/queryAll")
    public DBResult<List<TrainQueryRes>> queryAll() {
        return DBResult.success(service.queryAll());
    }


}
