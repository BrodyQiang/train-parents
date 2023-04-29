package com.train.bean.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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

    private Date createTime;

    private Date updateTime;

}