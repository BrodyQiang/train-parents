package com.train.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.train.bean.request.DailyTrainSeatQueryReq;
import com.train.bean.request.DailyTrainSeatSaveReq;
import com.train.bean.response.DailyTrainSeatQueryRes;
import com.train.common.response.DBPages;
import com.train.common.util.SnowUtil;
import com.train.domain.DailyTrainSeat;
import com.train.domain.DailyTrainSeatExample;
import com.train.mapper.DailyTrainSeatMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Mr.Liu
 * @email yxml2580@163.com
 * @createDate 2023-05-08 18:23:36
 */

@Service
public class DailyTrainSeatService {

    @Autowired
    private DailyTrainSeatMapper mapper;

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
}
