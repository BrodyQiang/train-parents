package com.train;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.core.env.Environment;

@SpringBootApplication
@EnableFeignClients("com.train.feign")
public class TrainBatchApplication {

    //日志管理
    private static final Logger LOG = LoggerFactory.getLogger(TrainBatchApplication.class);

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(TrainBatchApplication.class);
        Environment env = app.run(args).getEnvironment();
        LOG.info("启动成功！！");
        LOG.info("Job跑批服务地址: \thttp://127.0.0.1:{}", env.getProperty("server.port"));
        System.out.println("-----项目启动成功-----");
        System.out.printf("Job跑批服务地址: \thttp://127.0.0.1:%s \n", env.getProperty("server.port"));
    }

}
