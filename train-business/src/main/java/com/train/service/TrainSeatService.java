package com.train.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.train.bean.request.TrainSeatQueryReq;
import com.train.bean.request.TrainSeatSaveReq;
import com.train.bean.response.TrainSeatQueryRes;
import com.train.common.response.DBPages;
import com.train.common.util.SnowUtil;
import com.train.domain.TrainSeat;
import com.train.domain.TrainSeatExample;
import com.train.mapper.TrainSeatMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Mr.Liu
 * @email yxml2580@163.com
 * @createDate 2023-05-05 18:40:05
 */

@Service
public class TrainSeatService {

    @Autowired
    private TrainSeatMapper mapper;

    public void save(TrainSeatSaveReq bean) {

        TrainSeat trainSeat = BeanUtil.copyProperties(bean, TrainSeat.class);

        // 当前时间
        DateTime now = DateTime.now();

        if (ObjectUtil.isNull(trainSeat.getId())) {
            // 新增
            //使用雪花算法生成id
            trainSeat.setId(SnowUtil.getSnowflakeNextId());
            trainSeat.setCreateTime(now);
            trainSeat.setUpdateTime(now);

            mapper.insert(trainSeat);
        } else {
            trainSeat.setUpdateTime(now);
            mapper.updateByPrimaryKeySelective(trainSeat);
        }

    }

    public DBPages<TrainSeatQueryRes> queryList(TrainSeatQueryReq bean) {

        TrainSeatExample example = new TrainSeatExample();
        example.setOrderByClause("train_code asc,`index` asc");
        TrainSeatExample.Criteria criteria = example.createCriteria();
        // 添加查询条件 按照车次查询
        if (ObjectUtil.isNotEmpty(bean.getTrainCode())) {
            criteria.andTrainCodeEqualTo(bean.getTrainCode());
        }

        // 分页
        PageHelper.startPage(bean.getPageNum(), bean.getPageSize());

        List<TrainSeat> list = mapper.selectByExample(example);

        List<TrainSeatQueryRes> result = BeanUtil.copyToList(list, TrainSeatQueryRes.class);

        return DBPages.restPage(result);
    }

    public void delete(Long id) {
        mapper.deleteByPrimaryKey(id);
    }
}
