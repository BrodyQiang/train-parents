package com.train.controller.web;

import com.train.bean.request.DailyTrainTicketQueryReq;
import com.train.bean.response.DailyTrainTicketQueryRes;
import com.train.common.response.DBPages;
import com.train.service.DailyTrainTicketService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
* @author Mr.Liu
* @email yxml2580@163.com
* @createDate 2023-05-14 14:36:07
*/

@RestController
@RequestMapping("/dailyTrainTicket")
@Api(value = "DailyTrainTicketController", tags = "")
public class DailyTrainTicketWebController {

    @Autowired
    private DailyTrainTicketService service;

    @GetMapping("/queryList")
    public DBPages<DailyTrainTicketQueryRes> queryList(@Valid DailyTrainTicketQueryReq bean) {
        return service.queryList(bean);
    }

}
