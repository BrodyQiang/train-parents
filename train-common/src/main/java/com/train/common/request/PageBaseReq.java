package com.train.common.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

/**
 * @author Mr.Liu
 * @email yxml2580@163.com
 * @createDate 2023/4/23 15:18
 */

@Getter
@Setter
public class PageBaseReq {

    // 页码
    @NotNull(message = "页码不能为空")
    private Integer pageNum = 1;

    // 每页数量
    @NotNull(message = "每页数量不能为空")
    @Max(value = 100, message = "每页最大100条")
    private Integer pageSize = 30;

}
