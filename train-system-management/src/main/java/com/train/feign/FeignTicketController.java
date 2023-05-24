package com.train.feign;

import com.train.bean.request.TicketQueryReq;
import com.train.bean.request.TicketSaveReq;
import com.train.bean.response.TicketQueryRes;
import com.train.common.request.AccountTicketReq;
import com.train.common.response.DBPages;
import com.train.common.response.DBResult;
import com.train.service.TicketService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
* @author Mr.Liu
* @email yxml2580@163.com
* @createDate 2023-05-24 19:43:07
*/

@RestController
@RequestMapping("/feign/ticket")
@Api(value = "FeignTicketController", tags = "")
public class FeignTicketController {

    @Autowired
    private TicketService service;

    @PostMapping("/save")
    public DBResult save(@RequestBody @Valid AccountTicketReq bean) {
        service.save(bean);
        return DBResult.success();
    }

}
