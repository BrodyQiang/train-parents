package com.train.bean.request;


import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
* @author Mr.Liu
* @email yxml2580@163.com
* @createDate 2023-05-08 18:04:19
*/


@Data
public class DailyTrainStationSaveReq {


    /**
     * id
     */
    private Long id;

    /**
     * 日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    @NotNull(message = "【日期】不能为空")
    private Date date;

    /**
     * 车次编号
     */
    @NotBlank(message = "【车次编号】不能为空")
    private String trainCode;

    /**
     * 站序|第一站是0
     */
    @NotNull(message = "【站序】不能为空")
    private Integer stationIndex;

    /**
     * 站名
     */
    @NotBlank(message = "【站名】不能为空")
    private String name;

    /**
     * 站名拼音
     */
    @NotBlank(message = "【站名拼音】不能为空")
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
    @NotNull(message = "【里程（公里）】不能为空")
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