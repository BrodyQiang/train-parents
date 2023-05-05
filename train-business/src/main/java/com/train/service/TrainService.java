package com.train.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.train.bean.request.TrainQueryReq;
import com.train.bean.request.TrainSaveReq;
import com.train.bean.response.TrainQueryRes;
import com.train.common.response.DBPages;
import com.train.common.util.SnowUtil;
import com.train.domain.Train;
import com.train.domain.TrainExample;
import com.train.mapper.TrainMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Mr.Liu
 * @email yxml2580@163.com
 * @createDate 2023-05-05 18:32:50
 */

@Service
public class TrainService {

    @Autowired
    private TrainMapper mapper;

    public void save(TrainSaveReq bean) {

        Train train = BeanUtil.copyProperties(bean, Train.class);

        // 当前时间
        DateTime now = DateTime.now();

        if (ObjectUtil.isNull(train.getId())) {
            // 新增
            //使用雪花算法生成id
            train.setId(SnowUtil.getSnowflakeNextId());
            train.setCreateTime(now);
            train.setUpdateTime(now);

            mapper.insert(train);
        } else {
            train.setUpdateTime(now);
            mapper.updateByPrimaryKeySelective(train);
        }

    }

    public DBPages<TrainQueryRes> queryList(TrainQueryReq bean) {

        TrainExample example = new TrainExample();
        TrainExample.Criteria criteria = example.createCriteria();

        // 分页
        PageHelper.startPage(bean.getPageNum(), bean.getPageSize());

        List<Train> list = mapper.selectByExample(example);

        List<TrainQueryRes> result = BeanUtil.copyToList(list, TrainQueryRes.class);

        return DBPages.restPage(result);
    }

    public void delete(Long id) {
        mapper.deleteByPrimaryKey(id);
    }
}
