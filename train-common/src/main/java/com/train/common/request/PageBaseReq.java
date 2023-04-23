package com.train.common.request;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Mr.Liu
 * @email yxml2580@163.com
 * @createDate 2023/4/23 15:18
 */

@Getter
@Setter
public class PageBaseReq {

    // 页码
    private Integer pageNum = 1;

    // 每页数量
    private Integer pageSize = 10;

}
