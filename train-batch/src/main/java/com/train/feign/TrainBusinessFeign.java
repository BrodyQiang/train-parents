package com.train.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author Mr.Liu
 * @email yxml2580@163.com
 * @createDate 2023/5/14 12:18
 */
@FeignClient(name = "train-business",url = "http://127.0.0.1:10005/business")
public interface TrainBusinessFeign {

    @GetMapping("/admin/business/test")
    String test();
}
