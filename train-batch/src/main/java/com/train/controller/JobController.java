package com.train.controller;


import com.train.bean.request.CronJobReq;
import com.train.bean.response.CronJobRes;
import com.train.common.enums.ResultCodeEnum;
import com.train.common.response.DBResult;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@RestController
@RequestMapping(value = "/admin/job")
public class JobController {

    private static Logger LOG = LoggerFactory.getLogger(JobController.class);

    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;

    @RequestMapping(value = "/run")
    public DBResult<Object> run(@RequestBody CronJobReq cronJobReq) throws SchedulerException {
        String jobClassName = cronJobReq.getName();
        String jobGroupName = cronJobReq.getGroup();
        LOG.info("手动执行任务开始：{}, {}", jobClassName, jobGroupName);
        schedulerFactoryBean.getScheduler().triggerJob(JobKey.jobKey(jobClassName, jobGroupName));
        return DBResult.success();
    }

    @RequestMapping(value = "/add")
    public DBResult add(@RequestBody CronJobReq cronJobReq) {
        String jobClassName = cronJobReq.getName();
        String jobGroupName = cronJobReq.getGroup();
        String cronExpression = cronJobReq.getCronExpression();
        String description = cronJobReq.getDescription();
        LOG.info("创建定时任务开始：{}，{}，{}，{}", jobClassName, jobGroupName, cronExpression, description);
        DBResult result = new DBResult();

        try {
            // 通过SchedulerFactory获取一个调度器实例
            Scheduler sched = schedulerFactoryBean.getScheduler();

            // 启动调度器
            sched.start();

            //构建job信息
            JobDetail jobDetail = JobBuilder.newJob((Class<? extends Job>) Class.forName(jobClassName)).withIdentity(jobClassName, jobGroupName).build();

            //表达式调度构建器(即任务执行的时间)
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);

            //按新的cronExpression表达式构建一个新的trigger
            CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(jobClassName, jobGroupName).withDescription(description).withSchedule(scheduleBuilder).build();

            sched.scheduleJob(jobDetail, trigger);
            result.setMsg("创建定时任务成功");
        } catch (SchedulerException e) {
            LOG.error("创建定时任务失败:" + e);
            result.setCode(ResultCodeEnum.ERROR.getCode());
            result.setMsg("创建定时任务失败:调度异常");
        } catch (ClassNotFoundException e) {
            LOG.error("创建定时任务失败:" + e);
            result.setCode(ResultCodeEnum.ERROR.getCode());
            result.setMsg("创建定时任务失败：任务类不存在");
        }
        LOG.info("创建定时任务结束：{}", result);
        return result;
    }

    @RequestMapping(value = "/pause")
    public DBResult pause(@RequestBody CronJobReq cronJobReq) {
        String jobClassName = cronJobReq.getName();
        String jobGroupName = cronJobReq.getGroup();
        LOG.info("暂停定时任务开始：{}，{}", jobClassName, jobGroupName);
        DBResult result = new DBResult();
        try {
            Scheduler sched = schedulerFactoryBean.getScheduler();
            sched.pauseJob(JobKey.jobKey(jobClassName, jobGroupName));
            result.setMsg("暂停定时任务成功");
        } catch (SchedulerException e) {
            LOG.error("暂停定时任务失败:" + e);
            result.setCode(ResultCodeEnum.ERROR.getCode());
            result.setMsg("暂停定时任务失败:调度异常");
        }
        LOG.info("暂停定时任务结束：{}", result);
        return result;
    }

    @RequestMapping(value = "/resume")
    public DBResult resume(@RequestBody CronJobReq cronJobReq) {
        String jobClassName = cronJobReq.getName();
        String jobGroupName = cronJobReq.getGroup();
        LOG.info("重启定时任务开始：{}，{}", jobClassName, jobGroupName);
        DBResult result = new DBResult();
        try {
            Scheduler sched = schedulerFactoryBean.getScheduler();
            sched.resumeJob(JobKey.jobKey(jobClassName, jobGroupName));
            result.setMsg("重启定时任务成功");
        } catch (SchedulerException e) {
            LOG.error("重启定时任务失败:" + e);
            result.setCode(ResultCodeEnum.ERROR.getCode());
            result.setMsg("重启定时任务失败:调度异常");
        }
        LOG.info("重启定时任务结束：{}", result);
        return result;
    }

    @RequestMapping(value = "/reschedule")
    public DBResult reschedule(@RequestBody CronJobReq cronJobReq) {
        String jobClassName = cronJobReq.getName();
        String jobGroupName = cronJobReq.getGroup();
        String cronExpression = cronJobReq.getCronExpression();
        String description = cronJobReq.getDescription();
        LOG.info("更新定时任务开始：{}，{}，{}，{}", jobClassName, jobGroupName, cronExpression, description);
        DBResult result = new DBResult();
        try {
            Scheduler scheduler = schedulerFactoryBean.getScheduler();
            TriggerKey triggerKey = TriggerKey.triggerKey(jobClassName, jobGroupName);
            // 表达式调度构建器
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);
            CronTriggerImpl trigger1 = (CronTriggerImpl) scheduler.getTrigger(triggerKey);
            trigger1.setStartTime(new Date()); // 重新设置开始时间
            CronTrigger trigger = trigger1;

            // 按新的cronExpression表达式重新构建trigger
            trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withDescription(description).withSchedule(scheduleBuilder).build();

            // 按新的trigger重新设置job执行
            scheduler.rescheduleJob(triggerKey, trigger);
            result.setMsg("更新定时任务成功");
        } catch (Exception e) {
            LOG.error("更新定时任务失败:" + e);
            result.setCode(ResultCodeEnum.ERROR.getCode());
            result.setMsg("更新定时任务失败:调度异常");
        }
        LOG.info("更新定时任务结束：{}", result);
        return result;
    }

    @RequestMapping(value = "/delete")
    public DBResult delete(@RequestBody CronJobReq cronJobReq) {
        String jobClassName = cronJobReq.getName();
        String jobGroupName = cronJobReq.getGroup();
        LOG.info("删除定时任务开始：{}，{}", jobClassName, jobGroupName);
        DBResult result = new DBResult();
        try {
            Scheduler scheduler = schedulerFactoryBean.getScheduler();
            scheduler.pauseTrigger(TriggerKey.triggerKey(jobClassName, jobGroupName));
            scheduler.unscheduleJob(TriggerKey.triggerKey(jobClassName, jobGroupName));
            scheduler.deleteJob(JobKey.jobKey(jobClassName, jobGroupName));
            result.setMsg("删除定时任务成功");
        } catch (SchedulerException e) {
            LOG.error("删除定时任务失败:" + e);
            result.setCode(ResultCodeEnum.ERROR.getCode());
            result.setMsg("删除定时任务失败:调度异常");
        }
        LOG.info("删除定时任务结束：{}", result);
        return result;
    }

    @RequestMapping(value = "/query")
    public DBResult query() {
        LOG.info("查看所有定时任务开始");
        DBResult result = new DBResult();
        List<CronJobRes> cronJobDtoList = new ArrayList();
        try {
            Scheduler scheduler = schedulerFactoryBean.getScheduler();
            for (String groupName : scheduler.getJobGroupNames()) {
                for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
                    CronJobRes res = new CronJobRes();
                    res.setName(jobKey.getName());
                    res.setGroup(jobKey.getGroup());

                    //get job's trigger
                    List<Trigger> triggers = (List<Trigger>) scheduler.getTriggersOfJob(jobKey);
                    CronTrigger cronTrigger = (CronTrigger) triggers.get(0);
                    res.setNextFireTime(cronTrigger.getNextFireTime());
                    res.setPreFireTime(cronTrigger.getPreviousFireTime());
                    res.setCronExpression(cronTrigger.getCronExpression());
                    res.setDescription(cronTrigger.getDescription());
                    Trigger.TriggerState triggerState = scheduler.getTriggerState(cronTrigger.getKey());
                    res.setState(triggerState.name());

                    cronJobDtoList.add(res);
                }
            }
            result.setMsg("操作成功");
        } catch (SchedulerException e) {
            LOG.error("查看定时任务失败:" + e);
            result.setCode(ResultCodeEnum.ERROR.getCode());
            result.setMsg("查看定时任务失败:调度异常");
        }
        result.setResult(cronJobDtoList);
        LOG.info("查看定时任务结束：{}", result);
        return result;
    }

}
