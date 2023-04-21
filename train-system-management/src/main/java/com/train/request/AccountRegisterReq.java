package com.train.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author Mr.Liu
 * @email yxml2580@163.com
 * @createDate 2023/4/21 21:17
 */
@Data
public class AccountRegisterReq {

    @NotBlank(message = "手机号不能为空")
    private String mobile;
}
