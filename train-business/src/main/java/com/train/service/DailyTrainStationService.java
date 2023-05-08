package com.train.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.train.bean.request.DailyTrainStationQueryReq;
import com.train.bean.request.DailyTrainStationSaveReq;
import com.train.bean.response.DailyTrainStationQueryRes;
import com.train.common.response.DBPages;
import com.train.common.util.SnowUtil;
import com.train.domain.DailyTrainStation;
import com.train.domain.DailyTrainStationExample;
import com.train.mapper.DailyTrainStationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Mr.Liu
 * @email yxml2580@163.com
 * @createDate 2023-05-08 18:04:19
 */

@Service
public class DailyTrainStationService {

    @Autowired
    private DailyTrainStationMapper mapper;

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
}
