package com.train;

import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.train.mapper")
@EnableScheduling
public class TrainSystemManagementApplication {

    //日志管理
    private static final Logger LOG = LoggerFactory.getLogger(TrainSystemManagementApplication.class);

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(TrainSystemManagementApplication.class);
        Environment env = app.run(args).getEnvironment();
        LOG.info("启动成功！！");
        LOG.info("系统支撑服务地址: \thttp://127.0.0.1:{}", env.getProperty("server.port"));
        LOG.info("谢李康的分支");
        System.out.println("-----项目启动成功-----");
        System.out.printf("系统支撑服务地址: \thttp://127.0.0.1:%s", env.getProperty("server.port"));
    }

}
