package com.train.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Mr.Liu
 * @email yxml2580@163.com
 * @createDate 2023/4/20 19:55
 */
@RestController
@RequestMapping("/admin/business")
@RefreshScope // 动态刷新配置
public class TestController {

    @Value("${test.nacos}")
    private String nacos;

    @GetMapping("/test")
    public String test() {
        return "Holle World ---------------测试feign调用" + nacos;
    }

}
