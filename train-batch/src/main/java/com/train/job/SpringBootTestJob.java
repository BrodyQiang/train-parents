package com.train.job;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author Mr.Liu
 * @email yxml2580@163.com
 * @createDate 2023/5/7 11:38
 * 适合单体应用，不适合集群
 * 没法实时更改定时任务状态和策略
 */
@Component
@EnableScheduling
public class SpringBootTestJob {

        //@Scheduled(cron = "0/5 * * * * ?")
        @Scheduled(cron = "0/5 * * * * ?")
        public void test() {
            // 增加分布式锁，解决集群问题
            System.out.println("SpringBootTestJob.test()");
        }
}
