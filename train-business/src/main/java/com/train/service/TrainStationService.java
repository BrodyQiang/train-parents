package com.train.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.train.bean.request.TrainStationQueryReq;
import com.train.bean.request.TrainStationSaveReq;
import com.train.bean.response.TrainStationQueryRes;
import com.train.common.enums.BusinessExceptionEnum;
import com.train.common.exception.BusinessException;
import com.train.common.response.DBPages;
import com.train.common.util.SnowUtil;
import com.train.domain.Train;
import com.train.domain.TrainExample;
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

            // 保存之前，先校验唯一键是否存在
            TrainStation trainStationDB = selectByUnique(bean.getTrainCode(), bean.getStationIndex());
            if (ObjectUtil.isNotEmpty(trainStationDB)) {
                throw new BusinessException(BusinessExceptionEnum.BUSINESS_TRAIN_STATION_INDEX_UNIQUE_ERROR);
            }
            // 保存之前，先校验唯一键是否存在
            trainStationDB = selectByUnique(bean.getTrainCode(), bean.getName());
            if (ObjectUtil.isNotEmpty(trainStationDB)) {
                throw new BusinessException(BusinessExceptionEnum.BUSINESS_TRAIN_STATION_NAME_UNIQUE_ERROR);
            }

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

    private TrainStation selectByUnique(String trainCode, Integer stationIndex) {
        TrainStationExample trainStationExample = new TrainStationExample();
        trainStationExample.createCriteria()
                .andTrainCodeEqualTo(trainCode)
                .andStationIndexEqualTo(stationIndex);
        List<TrainStation> list = mapper.selectByExample(trainStationExample);
        if (CollUtil.isNotEmpty(list)) {
            return list.get(0);
        } else {
            return null;
        }
    }

    private TrainStation selectByUnique(String trainCode, String name) {
        TrainStationExample trainStationExample = new TrainStationExample();
        trainStationExample.createCriteria()
                .andTrainCodeEqualTo(trainCode)
                .andNameEqualTo(name);
        List<TrainStation> list = mapper.selectByExample(trainStationExample);
        if (CollUtil.isNotEmpty(list)) {
            return list.get(0);
        } else {
            return null;
        }
    }

    /***
     * @author Mr.Liu
     * @date 2023/5/14 13:21
     * @param trainCode 车次
     * @return List<TrainStation> 根据车次查询车站信息
     */
    public List<TrainStation> selectByTrainCode(String trainCode) {
        TrainStationExample trainStationExample = new TrainStationExample();
        trainStationExample.setOrderByClause("station_index asc"); // 车站序号排序
        trainStationExample.createCriteria()
                .andTrainCodeEqualTo(trainCode);
        return mapper.selectByExample(trainStationExample);
    }
}


