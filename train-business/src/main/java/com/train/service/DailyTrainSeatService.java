package com.train.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.train.bean.request.DailyTrainSeatQueryReq;
import com.train.bean.request.DailyTrainSeatSaveReq;
import com.train.bean.response.DailyTrainSeatQueryRes;
import com.train.common.response.DBPages;
import com.train.common.util.SnowUtil;
import com.train.domain.DailyTrainSeat;
import com.train.domain.DailyTrainSeatExample;
import com.train.domain.TrainSeat;
import com.train.domain.TrainStation;
import com.train.mapper.DailyTrainSeatMapper;
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
 * @createDate 2023-05-08 18:23:36
 */

@Service
public class DailyTrainSeatService {

    private static final Logger LOG = LoggerFactory.getLogger(DailyTrainSeatService.class);

    @Autowired
    private DailyTrainSeatMapper mapper;

    @Autowired
    private TrainSeatService trainSeatService;

    @Autowired
    private TrainStationService trainStationService;

    public void save(DailyTrainSeatSaveReq bean) {

        DailyTrainSeat dailyTrainSeat = BeanUtil.copyProperties(bean, DailyTrainSeat.class);

        // 当前时间
        DateTime now = DateTime.now();

        if (ObjectUtil.isNull(dailyTrainSeat.getId())) {
            // 新增
            //使用雪花算法生成id
            dailyTrainSeat.setId(SnowUtil.getSnowflakeNextId());
            dailyTrainSeat.setCreateTime(now);
            dailyTrainSeat.setUpdateTime(now);

            mapper.insert(dailyTrainSeat);
        } else {
            dailyTrainSeat.setUpdateTime(now);
            mapper.updateByPrimaryKeySelective(dailyTrainSeat);
        }

    }

    public DBPages<DailyTrainSeatQueryRes> queryList(DailyTrainSeatQueryReq bean) {

        DailyTrainSeatExample example = new DailyTrainSeatExample();
        example.setOrderByClause("date asc,train_code asc,carriage_index asc,carriage_seat_index asc");
        DailyTrainSeatExample.Criteria criteria = example.createCriteria();
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

        List<DailyTrainSeat> list = mapper.selectByExample(example);

        List<DailyTrainSeatQueryRes> result = BeanUtil.copyToList(list, DailyTrainSeatQueryRes.class);

        return DBPages.restPage(result);
    }

    public void delete(Long id) {
        mapper.deleteByPrimaryKey(id);
    }

    /***
     * @author Mr.Liu
     * @date 2023/5/14 13:56
     * @param date 日期
     * @param trainCode 车次
     */
    @Transactional
    public void genDaily(Date date, String trainCode) {
        LOG.info("生成当天的车次座位信息，date={},trainCode={}", DateUtil.formatDate(date), trainCode);
        // 删除当天的车次座位信息
        DailyTrainSeatExample example = new DailyTrainSeatExample();
        example.createCriteria().andDateEqualTo(date).andTrainCodeEqualTo(trainCode);
        mapper.deleteByExample(example);

        // 查询基础车次座位信息
        List<TrainSeat> trainSeats = trainSeatService.selectByTrainCode(trainCode);
        if (CollUtil.isEmpty(trainSeats)) {
            LOG.info("车次座位信息为空，无法生成当天的车次座位信息");
            return;
        }

        // 查询车次站点信息
        List<TrainStation> trainStations = trainStationService.selectByTrainCode(trainCode);
        String sell = StrUtil.fillBefore("", '0', trainStations.size() - 1);

        // 生成当天的车次座位信息
        trainSeats.forEach(trainSeat -> {
            DailyTrainSeat dailyTrainSeat = BeanUtil.copyProperties(trainSeat, DailyTrainSeat.class);
            //当前时间
            DateTime now = DateTime.now();
            dailyTrainSeat.setId(SnowUtil.getSnowflakeNextId());
            dailyTrainSeat.setDate(date);
            dailyTrainSeat.setCreateTime(now);
            dailyTrainSeat.setUpdateTime(now);
            dailyTrainSeat.setSell(sell);
            mapper.insert(dailyTrainSeat);
        });
        LOG.info("生成当天的车次座位信息成功，date={},trainCode={}", DateUtil.formatDate(date), trainCode);
    }

    /***
     * @author Mr.Liu
     * @date 2023/5/14 20:33
     * @param date 日期
     * @param trainCode 车次
     * @param seatType 座位类型
     * @return int 根据日期、车次、座位类型查询剩余座位数
     */
    public int countSeat(Date date, String trainCode, String seatType) {
        DailyTrainSeatExample example = new DailyTrainSeatExample();
        example.createCriteria().andDateEqualTo(date).andTrainCodeEqualTo(trainCode).andSeatTypeEqualTo(seatType);
        long count = mapper.countByExample(example);
        if (count == 0L) {
            return -1;
        }
        return (int) count;
    }

    /***
     * @author Mr.Liu
     * @date 2023/5/22 19:38
     * @param date 时间
     * @param trainCode 车次
     * @param carriageIndex 车厢号
     * @return List<DailyTrainSeat> 根据日期、车次、车厢号查询座位信息
     */
    public List<DailyTrainSeat> selectByCarriage(Date date, String trainCode, Integer carriageIndex) {
        DailyTrainSeatExample example = new DailyTrainSeatExample();
        example.createCriteria()
                .andDateEqualTo(date)
                .andTrainCodeEqualTo(trainCode)
                .andCarriageIndexEqualTo(carriageIndex);
        return mapper.selectByExample(example);
    }
}
