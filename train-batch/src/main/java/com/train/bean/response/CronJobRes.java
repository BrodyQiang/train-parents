package com.train.bean.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Date;

/***
 * @author Mr.Liu
 * @date 2023/5/7 13:52
 * @description 定时任务返回实体
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Data
public class CronJobRes {

    // 任务组
    private String group;

    // 任务名称
    private String name;

    // 任务描述
    private String description;

    // 任务状态
    private String state;

    // 定时任务表达式
    private String cronExpression;

    // 时间
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date nextFireTime;

    // 时间
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date preFireTime;

}
