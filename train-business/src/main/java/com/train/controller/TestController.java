package com.train.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.train.common.feign.AccountFeignTicket;
import com.train.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private TestService testService;


    @Value("${test.nacos}")
    private String nacos;

    @Autowired
    private AccountFeignTicket accountFeignTicket;


    @GetMapping("/test")
    public String test() {
        String test = accountFeignTicket.test();
        return "Holle World ---------------测试feign调用" + nacos + test;
    }

    @SentinelResource("hello")
    @GetMapping("/hello")
    public String hello() throws InterruptedException {
        testService.hello2();
        return "Hello World! Business!";
    }

    @SentinelResource("hello1")
    @GetMapping("/hello1")
    public String hello1() throws InterruptedException {
        testService.hello2();
        return "Hello World! Business1!";
    }

}
