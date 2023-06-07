package com.train;

import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@MapperScan("com.train.mapper")
@EnableScheduling
@EnableFeignClients("com.train.common.feign")
@EnableCaching //开启缓存
public class TrainBusinessApplication {

    //日志管理
    private static final Logger LOG = LoggerFactory.getLogger(TrainBusinessApplication.class);

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(TrainBusinessApplication.class);
        Environment env = app.run(args).getEnvironment();
        LOG.info("启动成功！！");
        LOG.info("业务模块地址: \thttp://127.0.0.1:{}", env.getProperty("server.port"));
        System.out.println("-----项目启动成功-----");
        System.out.printf("业务模块地址: \thttp://127.0.0.1:%s \n", env.getProperty("server.port"));
        // 限流规则
//        initFlowRules();
//        LOG.info("已定义限流规则");
//        System.out.println("已定义限流规则");
    }

    private static void initFlowRules() {
        List<FlowRule> rules = new ArrayList<>();
        FlowRule rule = new FlowRule();
        rule.setResource("doConfirm");
        rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        // Set limit QPS to 20.
        rule.setCount(1);
        rules.add(rule);
        FlowRuleManager.loadRules(rules);
    }
}
