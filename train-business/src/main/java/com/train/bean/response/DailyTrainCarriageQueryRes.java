package com.train.bean.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
* @author Mr.Liu
* @email yxml2580@163.com
* @createDate 2023-05-08 18:11:12
*/


@Data
public class DailyTrainCarriageQueryRes {

    /**
     * id
     */
    @JsonSerialize(using= ToStringSerializer.class)
    private Long id;

    /**
     * 日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date date;

    /**
     * 车次编号
     */
    private String trainCode;

    /**
     * 箱序
     */
    private Integer index;

    /**
     * 座位类型|枚举[SeatTypeEnum]
     */
    private String seatType;

    /**
     * 座位数
     */
    private Integer seatCount;

    /**
     * 排数
     */
    private Integer rowCount;

    /**
     * 列数
     */
    private Integer colCount;

    /**
     * 新增时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;

    /**
     * 修改时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date updateTime;

}