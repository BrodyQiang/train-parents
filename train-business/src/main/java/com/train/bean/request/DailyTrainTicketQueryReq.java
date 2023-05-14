package com.train.bean.request;

import com.train.common.request.PageBaseReq;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
* @author Mr.Liu
* @email yxml2580@163.com
* @createDate 2023-05-14 14:36:07
 */

@Data
public class DailyTrainTicketQueryReq extends PageBaseReq {

    /**
     * 日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date date;

    /**
     * 车次编号
     */
    private String trainCode;

    /**
     * 出发站
     */
    private String start;

    /**
     * 到达站
     */
    private String end;
}