package com.train.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.train.bean.request.DailyTrainQueryReq;
import com.train.bean.request.DailyTrainSaveReq;
import com.train.bean.response.DailyTrainQueryRes;
import com.train.common.response.DBPages;
import com.train.common.util.SnowUtil;
import com.train.domain.DailyTrain;
import com.train.domain.DailyTrainExample;
import com.train.mapper.DailyTrainMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Mr.Liu
 * @email yxml2580@163.com
 * @createDate 2023-05-08 17:55:58
 */

@Service
public class DailyTrainService {

    @Autowired
    private DailyTrainMapper mapper;

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
}
