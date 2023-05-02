package com.train.bean.request;

import com.train.common.request.PageBaseReq;
import lombok.Data;

import java.util.Date;

@Data
public class AccountPassengerQueryReq extends PageBaseReq {


    private Long memberId;

    private String name;

    private String idCard;

    private String phone;

    private String type;

    private Date createTime;

    private Date updateTime;

}