package com.train.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjUtil;
import com.github.pagehelper.PageHelper;
import com.train.bean.request.TicketQueryReq;
import com.train.bean.response.TicketQueryRes;
import com.train.common.request.AccountTicketReq;
import com.train.common.response.DBPages;
import com.train.common.util.SnowUtil;
import com.train.domain.Ticket;
import com.train.domain.TicketExample;
import com.train.mapper.TicketMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Mr.Liu
 * @email yxml2580@163.com
 * @createDate 2023-05-24 19:43:07
 */

@Service
public class TicketService {

    private static final Logger LOG = LoggerFactory.getLogger(TicketService.class);


    @Autowired
    private TicketMapper mapper;

    /***
     * @author Mr.Liu
     * @date 2023/5/24 19:48
     * @param bean 该功能只有新增，没有修改
     */
    public void save(AccountTicketReq bean) {

        //LOG.info("seata全局事务ID save: {}", RootContext.getXID());

        Ticket ticket = BeanUtil.copyProperties(bean, Ticket.class);
        // 当前时间
        DateTime now = DateTime.now();
        ticket.setId(SnowUtil.getSnowflakeNextId());
        ticket.setCreateTime(now);
        ticket.setUpdateTime(now);
        mapper.insert(ticket);
        // 模拟被调用方出现异常
        // if (1 == 1) {
        //     throw new Exception("测试异常11");
        // }
    }

    public DBPages<TicketQueryRes> queryList(TicketQueryReq bean) {

        TicketExample example = new TicketExample();
        TicketExample.Criteria criteria = example.createCriteria();

        if (ObjUtil.isNotNull(bean.getMemberId())) {
            criteria.andMemberIdEqualTo(bean.getMemberId());
        }

        // 分页
        PageHelper.startPage(bean.getPageNum(), bean.getPageSize());

        List<Ticket> list = mapper.selectByExample(example);

        List<TicketQueryRes> result = BeanUtil.copyToList(list, TicketQueryRes.class);

        return DBPages.restPage(result);
    }

    public void delete(Long id) {
        mapper.deleteByPrimaryKey(id);
    }
}
