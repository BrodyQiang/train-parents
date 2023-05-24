package com.train.bean.request;

import com.train.common.request.PageBaseReq;
import lombok.Data;

/**
* @author Mr.Liu
* @email yxml2580@163.com
* @createDate 2023-05-24 19:43:07
 */

@Data
public class TicketQueryReq extends PageBaseReq {

    private Long memberId;
}