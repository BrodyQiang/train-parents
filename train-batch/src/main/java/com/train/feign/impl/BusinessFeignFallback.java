package com.train.feign.impl;

import com.train.common.response.DBResult;
import com.train.feign.TrainBusinessFeign;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author Mr.Liu
 * @email yxml2580@163.com
 * @createDate 2023/6/10 16:58
 */
@Component // 必须添加，否则不会生效
public class BusinessFeignFallback implements TrainBusinessFeign {

    @Override
    public String test() {
        return "Fallback";
    }

    @Override
    public DBResult genDaily(Date date) {
        return null;
    }
}
