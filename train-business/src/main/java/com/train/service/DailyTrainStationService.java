package com.train.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.train.bean.request.DailyTrainStationQueryReq;
import com.train.bean.request.DailyTrainStationSaveReq;
import com.train.bean.response.DailyTrainStationQueryRes;
import com.train.common.response.DBPages;
import com.train.common.util.SnowUtil;
import com.train.domain.DailyTrainStation;
import com.train.domain.DailyTrainStationExample;
import com.train.domain.TrainStation;
import com.train.mapper.DailyTrainStationMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author Mr.Liu
 * @email yxml2580@163.com
 * @createDate 2023-05-08 18:04:19
 */

@Service
public class DailyTrainStationService {

    private static final Logger LOG = LoggerFactory.getLogger(DailyTrainStationService.class);

    @Autowired
    private DailyTrainStationMapper mapper;

    @Autowired
    private TrainStationService trainStationService;

    public void save(DailyTrainStationSaveReq bean) {

        DailyTrainStation dailyTrainStation = BeanUtil.copyProperties(bean, DailyTrainStation.class);

        // 当前时间
        DateTime now = DateTime.now();

        if (ObjectUtil.isNull(dailyTrainStation.getId())) {
            // 新增
            //使用雪花算法生成id
            dailyTrainStation.setId(SnowUtil.getSnowflakeNextId());
            dailyTrainStation.setCreateTime(now);
            dailyTrainStation.setUpdateTime(now);

            mapper.insert(dailyTrainStation);
        } else {
            dailyTrainStation.setUpdateTime(now);
            mapper.updateByPrimaryKeySelective(dailyTrainStation);
        }

    }

    public DBPages<DailyTrainStationQueryRes> queryList(DailyTrainStationQueryReq bean) {

        DailyTrainStationExample example = new DailyTrainStationExample();
        example.setOrderByClause("date desc, train_code asc,station_index asc");
        DailyTrainStationExample.Criteria criteria = example.createCriteria();
        // 查询条件 选择查询那天的车次
        if (ObjectUtil.isNotNull(bean.getDate())) {
            criteria.andDateEqualTo(bean.getDate());
        }
        // 查询条件 选择查询那天的车次
        if (ObjectUtil.isNotEmpty(bean.getTrainCode())) {
            criteria.andTrainCodeEqualTo(bean.getTrainCode());
        }

        // 分页
        PageHelper.startPage(bean.getPageNum(), bean.getPageSize());

        List<DailyTrainStation> list = mapper.selectByExample(example);

        List<DailyTrainStationQueryRes> result = BeanUtil.copyToList(list, DailyTrainStationQueryRes.class);

        return DBPages.restPage(result);
    }

    public void delete(Long id) {
        mapper.deleteByPrimaryKey(id);
    }

    /***
     * @author Mr.Liu
     * @date 2023/5/14 13:16
     * @param date 生成某天的车站信息
     * @param trainCode 某日车站的车次号
     */
    @Transactional
    public void genDaily(Date date, String trainCode) {
        LOG.info("生成某天的车站信息，date:{},trainCode:{}", DateUtil.formatDate(date), trainCode);

        // 删除当天的车站信息
        DailyTrainStationExample example = new DailyTrainStationExample();
        example.createCriteria().andDateEqualTo(date).andTrainCodeEqualTo(trainCode);
        mapper.deleteByExample(example);

        // 查询某车次的基础车次信息
        List<TrainStation> trainStations = trainStationService.selectByTrainCode(trainCode);

        if (CollUtil.isEmpty(trainStations)) {
            LOG.info("查询某车次的基础车站信息为空");
            return;
        }

        // 生成当天的车站信息
        trainStations.forEach( trainStation -> {

            DailyTrainStation dailyTrainStation = BeanUtil.copyProperties(trainStation, DailyTrainStation.class);
            // 当前时间
            DateTime now = DateTime.now();
            dailyTrainStation.setId(SnowUtil.getSnowflakeNextId());
            dailyTrainStation.setCreateTime(now);
            dailyTrainStation.setUpdateTime(now);
            dailyTrainStation.setDate(date);
            mapper.insert(dailyTrainStation);
        });
        LOG.info("生成某天的车站信息完成，date:{},trainCode:{}", DateUtil.formatDate(date), trainCode);
    }

    /***
     * @author Mr.Liu
     * @date 2023/6/10 17:43
     * @param trainCode 车次号
     * @param date 日期
     * @return long  按车次查询全部车站
     */
    public long countByTrainCode(Date date,String trainCode) {
        DailyTrainStationExample example = new DailyTrainStationExample();
        example.createCriteria().andDateEqualTo(date).andTrainCodeEqualTo(trainCode);
        return mapper.countByExample(example);
    }
}
