package com.train.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.train.bean.request.DailyTrainCarriageQueryReq;
import com.train.bean.request.DailyTrainCarriageSaveReq;
import com.train.bean.response.DailyTrainCarriageQueryRes;
import com.train.common.response.DBPages;
import com.train.common.util.SnowUtil;
import com.train.domain.DailyTrainCarriage;
import com.train.domain.DailyTrainCarriageExample;
import com.train.domain.TrainCarriage;
import com.train.enums.SeatColEnum;
import com.train.mapper.DailyTrainCarriageMapper;
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
 * @createDate 2023-05-08 18:11:12
 */

@Service
public class DailyTrainCarriageService {

    private static final Logger LOG = LoggerFactory.getLogger(DailyTrainCarriageService.class);

    @Autowired
    private DailyTrainCarriageMapper mapper;

    @Autowired
    private TrainCarriageService trainCarriageService;

    public void save(DailyTrainCarriageSaveReq bean) {


        // 当前时间
        DateTime now = DateTime.now();

        // 自动计算出列数和总座位数
        List<SeatColEnum> seatColEnums = SeatColEnum.getColsByType(bean.getSeatType());
        bean.setColCount(seatColEnums.size());
        bean.setSeatCount(bean.getColCount() * bean.getRowCount());


        DailyTrainCarriage dailyTrainCarriage = BeanUtil.copyProperties(bean, DailyTrainCarriage.class);
        if (ObjectUtil.isNull(dailyTrainCarriage.getId())) {
            // 新增
            //使用雪花算法生成id
            dailyTrainCarriage.setId(SnowUtil.getSnowflakeNextId());
            dailyTrainCarriage.setCreateTime(now);
            dailyTrainCarriage.setUpdateTime(now);

            mapper.insert(dailyTrainCarriage);
        } else {
            dailyTrainCarriage.setUpdateTime(now);
            mapper.updateByPrimaryKeySelective(dailyTrainCarriage);
        }

    }

    public DBPages<DailyTrainCarriageQueryRes> queryList(DailyTrainCarriageQueryReq bean) {

        DailyTrainCarriageExample example = new DailyTrainCarriageExample();
        example.setOrderByClause("date desc, train_code asc,`index` asc");
        DailyTrainCarriageExample.Criteria criteria = example.createCriteria();
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

        List<DailyTrainCarriage> list = mapper.selectByExample(example);

        List<DailyTrainCarriageQueryRes> result = BeanUtil.copyToList(list, DailyTrainCarriageQueryRes.class);

        return DBPages.restPage(result);
    }

    public void delete(Long id) {
        mapper.deleteByPrimaryKey(id);
    }

    /***
     * @author Mr.Liu
     * @date 2023/5/14 13:48
     * @param date 时间
     * @param trainCode 车次
     */
    @Transactional
    public void genDaily(Date date, String trainCode) {

        LOG.info("生成当天的车厢信息，车次：{}，时间：{}", trainCode, DateUtil.formatDate(date));

        // 删除当天的车厢信息
        DailyTrainCarriageExample example = new DailyTrainCarriageExample();
        example.createCriteria().andDateEqualTo(date).andTrainCodeEqualTo(trainCode);
        mapper.deleteByExample(example);

        // 查询基础车厢信息
        List<TrainCarriage> trainCarriages = trainCarriageService.selectByTrainCode(trainCode);

        if (CollUtil.isEmpty(trainCarriages)) {
            LOG.error("车次{}没有车厢信息", trainCode);
            return;
        }
        trainCarriages.forEach(trainCarriage -> {
            // 生成当天的车厢信息
            DailyTrainCarriage dailyTrainCarriage = BeanUtil.copyProperties(trainCarriage, DailyTrainCarriage.class);
            // 当前时间
            DateTime now = DateTime.now();
            dailyTrainCarriage.setId(SnowUtil.getSnowflakeNextId());
            dailyTrainCarriage.setDate(date);
            dailyTrainCarriage.setCreateTime(now);
            dailyTrainCarriage.setUpdateTime(now);
            mapper.insert(dailyTrainCarriage);
        });
        LOG.info("生成当天的车厢信息完成，车次：{}，时间：{}", trainCode, DateUtil.formatDate(date));
    }

    /***
     * 根据车次、时间和座位类型查询
     * @param date 时间
     * @param trainCode 车次
     * @param seatType 座位类型
     * @return
     */
    public List<DailyTrainCarriage> selectBySeatType (Date date, String trainCode, String seatType) {
        DailyTrainCarriageExample example = new DailyTrainCarriageExample();
        example.createCriteria()
                .andDateEqualTo(date)
                .andTrainCodeEqualTo(trainCode)
                .andSeatTypeEqualTo(seatType);
        return mapper.selectByExample(example);
    }
}
