package com.train.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.train.bean.request.TrainCarriageQueryReq;
import com.train.bean.request.TrainCarriageSaveReq;
import com.train.bean.response.TrainCarriageQueryRes;
import com.train.common.response.DBPages;
import com.train.common.util.SnowUtil;
import com.train.domain.TrainCarriage;
import com.train.domain.TrainCarriageExample;
import com.train.mapper.TrainCarriageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Mr.Liu
 * @email yxml2580@163.com
 * @createDate 2023-05-05 18:37:23
 */

@Service
public class TrainCarriageService {

    @Autowired
    private TrainCarriageMapper mapper;

    public void save(TrainCarriageSaveReq bean) {

        TrainCarriage trainCarriage = BeanUtil.copyProperties(bean, TrainCarriage.class);

        // 当前时间
        DateTime now = DateTime.now();

        if (ObjectUtil.isNull(trainCarriage.getId())) {
            // 新增
            //使用雪花算法生成id
            trainCarriage.setId(SnowUtil.getSnowflakeNextId());
            trainCarriage.setCreateTime(now);
            trainCarriage.setUpdateTime(now);

            mapper.insert(trainCarriage);
        } else {
            trainCarriage.setUpdateTime(now);
            mapper.updateByPrimaryKeySelective(trainCarriage);
        }

    }

    public DBPages<TrainCarriageQueryRes> queryList(TrainCarriageQueryReq bean) {

        TrainCarriageExample example = new TrainCarriageExample();
        TrainCarriageExample.Criteria criteria = example.createCriteria();

        // 分页
        PageHelper.startPage(bean.getPageNum(), bean.getPageSize());

        List<TrainCarriage> list = mapper.selectByExample(example);

        List<TrainCarriageQueryRes> result = BeanUtil.copyToList(list, TrainCarriageQueryRes.class);

        return DBPages.restPage(result);
    }

    public void delete(Long id) {
        mapper.deleteByPrimaryKey(id);
    }
}
