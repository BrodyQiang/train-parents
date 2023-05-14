package com.train.controller;

import com.train.feign.TrainBusinessFeign;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Mr.Liu
 * @email yxml2580@163.com
 * @createDate 2023/4/20 19:55
 */
@RestController
@RequestMapping("/batch")
@Slf4j
public class TestController {

    @Autowired
    private TrainBusinessFeign trainBusinessFeign;

    @GetMapping("/test")
    public String test() {
        String test = trainBusinessFeign.test();
        log.info("test:{}", test);
        return "Holle World batch";
    }

}
