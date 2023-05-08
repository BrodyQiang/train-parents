package com.train.bean.request;

import com.train.common.request.PageBaseReq;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
* @author Mr.Liu
* @email yxml2580@163.com
* @createDate 2023-05-08 18:23:36
 */

@Data
public class DailyTrainSeatQueryReq extends PageBaseReq {

    // 查询车次时间
    @DateTimeFormat(pattern = "yyyy-MM-dd") // 日期格式化 用于接收前端传递的日期格式 前端传的是字符串
    private Date date;

    // 车次编码
    private String trainCode;

}