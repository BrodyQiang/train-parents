package com.train.bean.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;

/**
* @author Mr.Liu
* @email yxml2580@163.com
* @createDate 2023-05-08 18:04:19
*/


@Data
public class DailyTrainStationQueryRes {

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
     * 站序|第一站是0
     */
    private Integer stationIndex;

    /**
     * 站名
     */
    private String name;

    /**
     * 站名拼音
     */
    private String namePinyin;

    /**
     * 进站时间
     */
    @JsonFormat(pattern = "HH:mm:ss",timezone = "GMT+8")
    private Date inTime;

    /**
     * 出站时间
     */
    @JsonFormat(pattern = "HH:mm:ss",timezone = "GMT+8")
    private Date outTime;

    /**
     * 停站时长
     */
    @JsonFormat(pattern = "HH:mm:ss",timezone = "GMT+8")
    private Date stopTime;

    /**
     * 里程（公里）|从上一站到本站的距离
     */
    private BigDecimal km;

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