package com.train.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.train.bean.request.StationQueryReq;
import com.train.bean.request.StationSaveReq;
import com.train.bean.response.StationQueryRes;
import com.train.common.response.DBPages;
import com.train.common.util.SnowUtil;
import com.train.domain.Station;
import com.train.domain.StationExample;
import com.train.mapper.StationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Mr.Liu
 * @email yxml2580@163.com
 * @createDate 2023-05-04 19:58:40
 */

@Service
public class StationService {

    @Autowired
    private StationMapper mapper;

    public void save(StationSaveReq bean) {

        Station station = BeanUtil.copyProperties(bean, Station.class);

        // 当前时间
        DateTime now = DateTime.now();

        if (ObjectUtil.isNull(station.getId())) {
            // 新增
            //使用雪花算法生成id
            station.setId(SnowUtil.getSnowflakeNextId());
            station.setCreateTime(now);
            station.setUpdateTime(now);

            mapper.insert(station);
        } else {
            station.setUpdateTime(now);
            mapper.updateByPrimaryKeySelective(station);
        }

    }

    public DBPages<StationQueryRes> queryList(StationQueryReq bean) {

        StationExample example = new StationExample();
        example.setOrderByClause("id desc");
        StationExample.Criteria criteria = example.createCriteria();

        // 分页
        PageHelper.startPage(bean.getPageNum(), bean.getPageSize());

        List<Station> list = mapper.selectByExample(example);

        List<StationQueryRes> result = BeanUtil.copyToList(list, StationQueryRes.class);

        return DBPages.restPage(result);
    }

    public void delete(Long id) {
        mapper.deleteByPrimaryKey(id);
    }
}
