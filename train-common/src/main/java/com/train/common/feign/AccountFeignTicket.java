package com.train.common.feign;

import com.train.common.request.AccountTicketReq;
import com.train.common.response.DBResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * @author Mr.Liu
 * @email yxml2580@163.com
 * @createDate 2023/5/24 19:49
 */
// @FeignClient("member")
@FeignClient(name = "train-system-management", url = "http://127.0.0.1:10002/system/management") // 指定服务名 + url(使用哪个地址) 时，name属性必须指定，否则会报错
public interface AccountFeignTicket {

    @PostMapping("/feign/ticket/save")
    public DBResult save(@RequestBody @Valid AccountTicketReq bean);
}
