package com.train.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.train.bean.request.DailyTrainQueryReq;
import com.train.bean.request.DailyTrainSaveReq;
import com.train.bean.response.DailyTrainQueryRes;
import com.train.common.response.DBPages;
import com.train.common.util.SnowUtil;
import com.train.domain.DailyTrain;
import com.train.domain.DailyTrainExample;
import com.train.domain.Train;
import com.train.mapper.DailyTrainMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author Mr.Liu
 * @email yxml2580@163.com
 * @createDate 2023-05-08 17:55:58
 */

@Service
public class DailyTrainService {

    private static final Logger LOG = LoggerFactory.getLogger(DailyTrainService.class);

    @Autowired
    private DailyTrainMapper mapper;

    @Autowired
    private TrainService trainService;

    @Autowired
    private DailyTrainStationService dailyTrainStationService;

    @Autowired
    private DailyTrainCarriageService dailyTrainCarriageService;

    @Autowired
    private DailyTrainSeatService dailyTrainSeatService;

    public void save(DailyTrainSaveReq bean) {

        DailyTrain dailyTrain = BeanUtil.copyProperties(bean, DailyTrain.class);

        // 当前时间
        DateTime now = DateTime.now();

        if (ObjectUtil.isNull(dailyTrain.getId())) {
            // 新增
            //使用雪花算法生成id
            dailyTrain.setId(SnowUtil.getSnowflakeNextId());
            dailyTrain.setCreateTime(now);
            dailyTrain.setUpdateTime(now);

            mapper.insert(dailyTrain);
        } else {
            dailyTrain.setUpdateTime(now);
            mapper.updateByPrimaryKeySelective(dailyTrain);
        }

    }

    public DBPages<DailyTrainQueryRes> queryList(DailyTrainQueryReq bean) {

        DailyTrainExample example = new DailyTrainExample();
        example.setOrderByClause("date desc, code asc");
        DailyTrainExample.Criteria criteria = example.createCriteria();
        // 查询条件 选择查询那天的车次
        if (ObjectUtil.isNotNull(bean.getDate())) {
            criteria.andDateEqualTo(bean.getDate());
        }
        // 查询条件 选择查询那天的车次
        if (ObjectUtil.isNotEmpty(bean.getCode())) {
            criteria.andCodeEqualTo(bean.getCode());
        }

        // 分页
        PageHelper.startPage(bean.getPageNum(), bean.getPageSize());

        List<DailyTrain> list = mapper.selectByExample(example);

        List<DailyTrainQueryRes> result = BeanUtil.copyToList(list, DailyTrainQueryRes.class);

        return DBPages.restPage(result);
    }

    public void delete(Long id) {
        mapper.deleteByPrimaryKey(id);
    }

    /***
     * @author Mr.Liu
     * @date 2023/5/14 12:37
     * @param date 生成某日天的车次信息 包括车次信息、车站信息、车厢信息、座位信息

     */
    public void genDaily(Date date) {

        // 查询所有基础车次信息
        List<Train> trains = trainService.selectAll();
        if (CollUtil.isEmpty(trains)) {
            LOG.info("没有车次信息，无法生成当天车次信息");
            return;
        }

        // 生成当天的车次信息
        trains.forEach(train -> {
            getDailyTrain(date, train);
        });
    }

    /***
     * @author Mr.Liu
     * @date 2023/5/14 12:53
     * @param date 生成某日天的车次信息 包括车次信息、车站信息、车厢信息、座位信息
     * @param train 基础车次信息
     */
    private void getDailyTrain(Date date, Train train) {
        LOG.info("生成当天车次信息，车次：{}，日期：{}", train.getCode(), DateUtil.formatDate(date));
        // 先删除当天的车次信息
        DailyTrainExample example = new DailyTrainExample();
        example.createCriteria().andDateEqualTo(date).andCodeEqualTo(train.getCode());
        mapper.deleteByExample(example);

        // 当前时间
        DateTime now = DateTime.now();

        // 生成当天的车次信息
        DailyTrain dailyTrain = BeanUtil.copyProperties(train, DailyTrain.class);
        dailyTrain.setId(SnowUtil.getSnowflakeNextId());
        dailyTrain.setCreateTime(now);
        dailyTrain.setUpdateTime(now);
        dailyTrain.setDate(date);
        mapper.insert(dailyTrain);
        LOG.info("生成当天车次信息完成，车次：{}，日期：{}，车次信息：{}", train.getCode(), DateUtil.formatDate(date), dailyTrain);

        // 生成当天的车站信息
        dailyTrainStationService.genDaily(date, train.getCode());

        // 生成当天的车厢信息
        dailyTrainCarriageService.genDaily(date, train.getCode());

        // 生成当天的座位信息
        dailyTrainSeatService.genDaily(date, train.getCode());
    }
}
