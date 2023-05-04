package com.train.controller;

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
public class TestController {

    @GetMapping("/test")
    public String test() {
        return "Holle World";
    }

}
