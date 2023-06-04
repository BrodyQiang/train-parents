package com.train.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.train.bean.request.DailyTrainTicketQueryReq;
import com.train.bean.request.DailyTrainTicketSaveReq;
import com.train.bean.response.DailyTrainTicketQueryRes;
import com.train.common.response.DBPages;
import com.train.common.util.SnowUtil;
import com.train.domain.DailyTrain;
import com.train.domain.DailyTrainTicket;
import com.train.domain.DailyTrainTicketExample;
import com.train.domain.TrainStation;
import com.train.enums.SeatTypeEnum;
import com.train.enums.TrainTypeEnum;
import com.train.mapper.DailyTrainTicketMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

/**
 * @author Mr.Liu
 * @email yxml2580@163.com
 * @createDate 2023-05-14 14:36:07
 */

@Service
public class DailyTrainTicketService {

    private static final Logger LOG = LoggerFactory.getLogger(DailyTrainTicketService.class);

    @Autowired
    private DailyTrainTicketMapper mapper;

    @Autowired
    private TrainStationService trainStationService;

    @Autowired
    private DailyTrainSeatService dailyTrainSeatService;

    public void save(DailyTrainTicketSaveReq bean) {

        DailyTrainTicket dailyTrainTicket = BeanUtil.copyProperties(bean, DailyTrainTicket.class);

        // 当前时间
        DateTime now = DateTime.now();

        if (ObjectUtil.isNull(dailyTrainTicket.getId())) {
            // 新增
            //使用雪花算法生成id
            dailyTrainTicket.setId(SnowUtil.getSnowflakeNextId());
            dailyTrainTicket.setCreateTime(now);
            dailyTrainTicket.setUpdateTime(now);

            mapper.insert(dailyTrainTicket);
        } else {
            dailyTrainTicket.setUpdateTime(now);
            mapper.updateByPrimaryKeySelective(dailyTrainTicket);
        }

    }

    //@Cacheable(value = "dailyTrainTicket.queryList") // 缓存
    public DBPages<DailyTrainTicketQueryRes> queryList(DailyTrainTicketQueryReq bean) {

        // 常见的缓存过期策略
        // TTL 超时时间
        // LRU 最近最少使用
        // LFU 最近最不经常使用
        // FIFO 先进先出
        // Random 随机淘汰策略
        // 去缓存里取数据，因数据库本身就没数据而造成缓存穿透
        // if (有数据) { null []
        //     return
        // } else {
        //     去数据库取数据
        // }

        DailyTrainTicketExample example = new DailyTrainTicketExample();
        DailyTrainTicketExample.Criteria criteria = example.createCriteria();

        if (ObjUtil.isNotNull(bean.getDate())) {
            criteria.andDateEqualTo(bean.getDate());
        }
        if (ObjUtil.isNotEmpty(bean.getTrainCode())) {
            criteria.andTrainCodeEqualTo(bean.getTrainCode());
        }
        if (ObjUtil.isNotEmpty(bean.getStart())) {
            criteria.andStartEqualTo(bean.getStart());
        }
        if (ObjUtil.isNotEmpty(bean.getEnd())) {
            criteria.andEndEqualTo(bean.getEnd());
        }

        // 分页
        PageHelper.startPage(bean.getPageNum(), bean.getPageSize());

        List<DailyTrainTicket> list = mapper.selectByExample(example);

        List<DailyTrainTicketQueryRes> result = BeanUtil.copyToList(list, DailyTrainTicketQueryRes.class);

        return DBPages.restPage(result);
    }

    public void delete(Long id) {
        mapper.deleteByPrimaryKey(id);
    }

    /***
     * @author Mr.Liu
     * @date 2023/5/14 20:30
     * @param dailyTrain
     * @param date
     * @param trainCode
     */
    @Transactional
    public void genDaily(DailyTrain dailyTrain, Date date, String trainCode) {

        LOG.info("生成当天的车次的余票，车次：{}，日期：{}", trainCode, DateUtil.formatDate(date));

        // 删除当天的车次的余票
        DailyTrainTicketExample example = new DailyTrainTicketExample();
        example.createCriteria().andDateEqualTo(date).andTrainCodeEqualTo(trainCode);
        mapper.deleteByExample(example);

        // 查询某车次的所有的车站信息
        List<TrainStation> trainStations = trainStationService.selectByTrainCode(trainCode);
        if (CollUtil.isEmpty(trainStations)) {
            LOG.info("车次{}没有车站信息", trainCode);
            return;
        }

        // 查询某车次的所有座位类型的座位数
        int ydz = dailyTrainSeatService.countSeat(date, trainCode, SeatTypeEnum.YDZ.getCode());
        int edz = dailyTrainSeatService.countSeat(date, trainCode, SeatTypeEnum.EDZ.getCode());
        int rw = dailyTrainSeatService.countSeat(date, trainCode, SeatTypeEnum.RW.getCode());
        int yw = dailyTrainSeatService.countSeat(date, trainCode, SeatTypeEnum.YW.getCode());

        DateTime now = DateTime.now();
        // 生成当天的车次的余票
        for (int i = 0; i < trainStations.size(); i++) {
            // 起始站
            TrainStation startStation = trainStations.get(i);
            // 起始站到当前站的总公里数
            BigDecimal sumKM = BigDecimal.ZERO;
            for (int j = i + 1; j < trainStations.size(); j++) {
                // 结束站
                TrainStation endStation = trainStations.get(j);
                // 累加总公里数
                sumKM = sumKM.add(endStation.getKm());

                // 票价 = 里程之和 * 座位单价 * 车次类型系数
                String trainType = dailyTrain.getType();
                // 计算票价系数：TrainTypeEnum.priceRate
                BigDecimal priceRate = EnumUtil.getFieldBy(TrainTypeEnum::getPriceRate, TrainTypeEnum::getCode, trainType);
                BigDecimal ydzPrice = sumKM.multiply(SeatTypeEnum.YDZ.getPrice()).multiply(priceRate).setScale(2, RoundingMode.HALF_UP);
                BigDecimal edzPrice = sumKM.multiply(SeatTypeEnum.EDZ.getPrice()).multiply(priceRate).setScale(2, RoundingMode.HALF_UP);
                BigDecimal rwPrice = sumKM.multiply(SeatTypeEnum.RW.getPrice()).multiply(priceRate).setScale(2, RoundingMode.HALF_UP);
                BigDecimal ywPrice = sumKM.multiply(SeatTypeEnum.YW.getPrice()).multiply(priceRate).setScale(2, RoundingMode.HALF_UP);

                // 生成余票
                DailyTrainTicket dailyTrainTicket = new DailyTrainTicket();
                dailyTrainTicket.setId(SnowUtil.getSnowflakeNextId());
                dailyTrainTicket.setDate(date);
                dailyTrainTicket.setTrainCode(trainCode);
                dailyTrainTicket.setStart(startStation.getName());
                dailyTrainTicket.setStartPinyin(startStation.getNamePinyin());
                dailyTrainTicket.setStartTime(startStation.getOutTime());
                dailyTrainTicket.setStartIndex(startStation.getStationIndex());
                dailyTrainTicket.setEnd(endStation.getName());
                dailyTrainTicket.setEndPinyin(endStation.getNamePinyin());
                dailyTrainTicket.setEndTime(endStation.getInTime());
                dailyTrainTicket.setEndIndex(endStation.getStationIndex());
                dailyTrainTicket.setYdz(ydz);
                dailyTrainTicket.setYdzPrice(ydzPrice);
                dailyTrainTicket.setEdz(edz);
                dailyTrainTicket.setEdzPrice(edzPrice);
                dailyTrainTicket.setRw(rw);
                dailyTrainTicket.setRwPrice(rwPrice);
                dailyTrainTicket.setYw(yw);
                dailyTrainTicket.setYwPrice(ywPrice);
                dailyTrainTicket.setCreateTime(now);
                dailyTrainTicket.setUpdateTime(now);
                mapper.insert(dailyTrainTicket);
            }
        }

        LOG.info("生成当天的车次的余票，车次：{}，日期：{}，完成", trainCode, DateUtil.formatDate(date));
    }


    /***
     * 查询某车次的某天的余票
     * @param date
     * @param trainCode
     * @return
     */
    public DailyTrainTicket selectByUnique(Date date, String trainCode, String start, String end) {
        DailyTrainTicketExample dailyTrainTicketExample = new DailyTrainTicketExample();
        dailyTrainTicketExample.createCriteria()
                .andDateEqualTo(date)
                .andTrainCodeEqualTo(trainCode)
                .andStartEqualTo(start)
                .andEndEqualTo(end);
        List<DailyTrainTicket> list = mapper.selectByExample(dailyTrainTicketExample);
        if (CollUtil.isNotEmpty(list)) {
            return list.get(0);
        } else {
            return null;
        }
    }

}
