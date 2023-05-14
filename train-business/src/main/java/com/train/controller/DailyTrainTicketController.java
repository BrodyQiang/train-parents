package com.train.controller;

import com.train.bean.request.DailyTrainTicketQueryReq;
import com.train.bean.request.DailyTrainTicketSaveReq;
import com.train.bean.response.DailyTrainTicketQueryRes;
import com.train.common.response.DBPages;
import com.train.common.response.DBResult;
import com.train.service.DailyTrainTicketService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
* @author Mr.Liu
* @email yxml2580@163.com
* @createDate 2023-05-14 14:36:07
*/

@RestController
@RequestMapping("/dailyTrainTicket")
@Api(value = "DailyTrainTicketController", tags = "")
public class DailyTrainTicketController {

    @Autowired
    private DailyTrainTicketService service;

    @PostMapping("/save")
    public DBResult save(@RequestBody @Valid DailyTrainTicketSaveReq bean) {
        service.save(bean);
        return DBResult.success();
    }

    @GetMapping("/queryList")
    public DBPages<DailyTrainTicketQueryRes> queryList(@Valid DailyTrainTicketQueryReq bean) {
        return service.queryList(bean);
    }

    @DeleteMapping("/delete/{id}")
    public DBResult delete(@PathVariable Long id) {
        service.delete(id);
        return DBResult.success();
    }

}
