package com.train.bean.request;


import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
* @author Mr.Liu
* @email yxml2580@163.com
* @createDate 2023-05-05 18:37:23
*/


@Data
public class TrainCarriageSaveReq {


    /**
     * id
     */
    private Long id;

    /**
     * 车次编号
     */
    @NotBlank(message = "【车次编号】不能为空")
    private String trainCode;

    /**
     * 厢号
     */
    @NotNull(message = "【厢号】不能为空")
    private Integer index;

    /**
     * 座位类型|枚举[SeatTypeEnum]
     */
    @NotBlank(message = "【座位类型】不能为空")
    private String seatType;

    /**
     * 座位数
     */
    @NotNull(message = "【座位数】不能为空")
    private Integer seatCount;

    /**
     * 排数
     */
    @NotNull(message = "【排数】不能为空")
    private Integer rowCount;

    /**
     * 列数
     */
    @NotNull(message = "【列数】不能为空")
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