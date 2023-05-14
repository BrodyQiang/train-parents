package com.train.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.train.bean.request.DailyTrainTicketQueryReq;
import com.train.bean.request.DailyTrainTicketSaveReq;
import com.train.bean.response.DailyTrainTicketQueryRes;
import com.train.common.response.DBPages;
import com.train.common.util.SnowUtil;
import com.train.domain.DailyTrainTicket;
import com.train.domain.DailyTrainTicketExample;
import com.train.mapper.DailyTrainTicketMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Mr.Liu
 * @email yxml2580@163.com
 * @createDate 2023-05-14 14:36:07
 */

@Service
public class DailyTrainTicketService {

    @Autowired
    private DailyTrainTicketMapper mapper;

    public void save(DailyTrainTicketSaveReq bean) {

        DailyTrainTicket dailyTrainTicket = BeanUtil.copyProperties(bean, DailyTrainTicket.class);

        // 当前时间
        DateTime now = DateTime.now();

        if (ObjectUtil.isNull(dailyTrainTicket.getId())) {
            // 新增
            //使用雪花算法生成id
            dailyTrainTicket.setId(SnowUtil.getSnowflakeNextId());
            dailyTrainTicket.setCreateTime(now);
            dailyTrainTicket.setUpdateTime(now);

            mapper.insert(dailyTrainTicket);
        } else {
            dailyTrainTicket.setUpdateTime(now);
            mapper.updateByPrimaryKeySelective(dailyTrainTicket);
        }

    }

    public DBPages<DailyTrainTicketQueryRes> queryList(DailyTrainTicketQueryReq bean) {

        DailyTrainTicketExample example = new DailyTrainTicketExample();
        DailyTrainTicketExample.Criteria criteria = example.createCriteria();

        // 分页
        PageHelper.startPage(bean.getPageNum(), bean.getPageSize());

        List<DailyTrainTicket> list = mapper.selectByExample(example);

        List<DailyTrainTicketQueryRes> result = BeanUtil.copyToList(list, DailyTrainTicketQueryRes.class);

        return DBPages.restPage(result);
    }

    public void delete(Long id) {
        mapper.deleteByPrimaryKey(id);
    }
}
