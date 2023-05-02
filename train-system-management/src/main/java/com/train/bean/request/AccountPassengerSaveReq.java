package com.train.bean.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Date;

@Data
public class AccountPassengerSaveReq {

    private Long id;

    private Long memberId;

    @NotBlank(message = "【姓名】不能为空")
    private String name;

    @NotBlank(message = "【身份证号】不能为空")
    private String idCard;

    @NotBlank(message = "【手机号】不能为空")
    private String phone;

    @NotBlank(message = "【乘客类型】不能为空")
    private String type;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

}