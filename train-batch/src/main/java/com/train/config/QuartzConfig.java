//package com.train.config;
//
//import com.train.job.TestJob;
//import org.quartz.*;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
///**
// * @author Mr.Liu
// * @email yxml2580@163.com
// * @createDate 2023/5/7 11:41
// */
//@Configuration
//public class QuartzConfig {
//
//    /**
//     * 声明一个任务
//     *
//     * @return
//     */
//    @Bean
//    public JobDetail jobDetail() {
//        return JobBuilder.newJob(TestJob.class)
//                .withIdentity("TestJob", "test")
//                .storeDurably()
//                .build();
//    }
//
//    /***
//     * @author Mr.Liu
//     * @date 2023/5/7 11:43
//     * @return 声明一个触发器，什么时候触发这个任务
//     */
//    @Bean
//    public Trigger trigger() {
//        return TriggerBuilder.newTrigger()
//                .forJob(jobDetail())
//                .withIdentity("trigger", "trigger")
//                .startNow()
//                .withSchedule(CronScheduleBuilder.cronSchedule("*/2 * * * * ?"))
//                .build();
//    }
//
//
//}
