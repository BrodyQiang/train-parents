package com.train.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.train.bean.request.TrainStationQueryReq;
import com.train.bean.request.TrainStationSaveReq;
import com.train.bean.response.TrainStationQueryRes;
import com.train.common.response.DBPages;
import com.train.common.util.SnowUtil;
import com.train.domain.TrainStation;
import com.train.domain.TrainStationExample;
import com.train.mapper.TrainStationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Mr.Liu
 * @email yxml2580@163.com
 * @createDate 2023-05-05 18:35:01
 */

@Service
public class TrainStationService {

    @Autowired
    private TrainStationMapper mapper;

    public void save(TrainStationSaveReq bean) {

        TrainStation trainStation = BeanUtil.copyProperties(bean, TrainStation.class);

        // 当前时间
        DateTime now = DateTime.now();

        if (ObjectUtil.isNull(trainStation.getId())) {
            // 新增
            //使用雪花算法生成id
            trainStation.setId(SnowUtil.getSnowflakeNextId());
            trainStation.setCreateTime(now);
            trainStation.setUpdateTime(now);

            mapper.insert(trainStation);
        } else {
            trainStation.setUpdateTime(now);
            mapper.updateByPrimaryKeySelective(trainStation);
        }

    }

    public DBPages<TrainStationQueryRes> queryList(TrainStationQueryReq bean) {

        TrainStationExample example = new TrainStationExample();
        example.setOrderByClause("train_code asc,station_index asc");
        TrainStationExample.Criteria criteria = example.createCriteria();
        // 添加查询条件 按照车次查询
        if (ObjectUtil.isNotEmpty(bean.getTrainCode())) {
            criteria.andTrainCodeEqualTo(bean.getTrainCode());
        }

        // 分页
        PageHelper.startPage(bean.getPageNum(), bean.getPageSize());

        List<TrainStation> list = mapper.selectByExample(example);

        List<TrainStationQueryRes> result = BeanUtil.copyToList(list, TrainStationQueryRes.class);

        return DBPages.restPage(result);
    }

    public void delete(Long id) {
        mapper.deleteByPrimaryKey(id);
    }
}
