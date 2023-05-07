package com.train.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * @author Mr.Liu
 * @email yxml2580@163.com
 * @createDate 2023/5/7 11:41
 */
public class TestJob implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        System.out.println("TestJob.execute()");
    }
}
