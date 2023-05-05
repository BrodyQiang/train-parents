package com.train.bean.request;


import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
* @author Mr.Liu
* @email yxml2580@163.com
* @createDate 2023-05-04 19:58:40
*/


@Data
public class StationSaveReq {


    /**
     * id
     */
    private Long id;

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
     * 站名拼音首字母
     */
    @NotBlank(message = "【站名拼音首字母】不能为空")
    private String namePy;

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