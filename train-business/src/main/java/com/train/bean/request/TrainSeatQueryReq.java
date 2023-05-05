package com.train.bean.request;

import com.train.common.request.PageBaseReq;
import lombok.Data;

/**
* @author Mr.Liu
* @email yxml2580@163.com
* @createDate 2023-05-05 18:40:05
 */

@Data
public class TrainSeatQueryReq extends PageBaseReq {

    private String trainCode;

}