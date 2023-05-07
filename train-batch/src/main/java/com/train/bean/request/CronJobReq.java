package com.train.bean.request;

import lombok.Data;
/**
 * @ClassName CronJobReq
 * @Author Mr.Liu
 * @Date 2023/5/7 13:52
 * @Version 1.0
 * @Description 定时任务请求实体
 */
@Data
public class CronJobReq {

    // 任务组
    private String group;

    // 任务名称
    private String name;

    // 任务描述
    private String description;

    // 定时任务表达式
    private String cronExpression;

}
