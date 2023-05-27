package com.train.feign;

import com.train.common.response.DBResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Date;

/**
 * @author Mr.Liu
 * @email yxml2580@163.com
 * @createDate 2023/5/14 12:18
 */
//@FeignClient(name = "train-business", url = "http://127.0.0.1:10005/business") // 指定服务名 + url(使用哪个地址) 时，name属性必须指定，否则会报错
@FeignClient(name = "train-business") // 指定服务名
public interface TrainBusinessFeign {

    String BASE_URL = "/business";

    String URL = "/admin/dailyTrain";

    @GetMapping(BASE_URL + "/admin/business/test")
    String test();

    @GetMapping(BASE_URL + URL + "/genDaily/{date}")
    DBResult genDaily(@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date date);
}
