package com.train.job;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.train.common.response.DBResult;
import com.train.feign.TrainBusinessFeign;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

/**
 * @author Mr.Liu
 * @email yxml2580@163.com
 * @createDate 2023/5/14 12:33
 */
@DisallowConcurrentExecution // 不允许并发执行
public class DailyTrainJob implements Job {

    private static final Logger LOG = LoggerFactory.getLogger(DailyTrainJob.class);

    @Autowired
    private TrainBusinessFeign trainBusinessFeign;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        // 增加日志流水号
        MDC.put("LOG_ID", System.currentTimeMillis() + RandomUtil.randomString(3));
        LOG.info("生成15天后的车次数据开始");
        Date date = new Date();
        DateTime dateTime = DateUtil.offsetDay(date, 15); // 15天后的时间
        Date offsetDate = dateTime.toJdkDate(); // 转换为jdk的Date类型
        DBResult<Object> result = trainBusinessFeign.genDaily(offsetDate);
        LOG.info("生成15天后的车次数据结束，结果：{}", result);
    }

}
