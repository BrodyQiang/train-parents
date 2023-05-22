package com.train.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.train.bean.request.ConfirmOrderQueryReq;
import com.train.bean.request.ConfirmOrderSaveReq;
import com.train.bean.response.ConfirmOrderQueryRes;
import com.train.common.context.LoginAccountContext;
import com.train.common.interceptor.AccountInterceptor;
import com.train.common.response.DBPages;
import com.train.common.util.SnowUtil;
import com.train.domain.ConfirmOrder;
import com.train.domain.ConfirmOrderExample;
import com.train.enums.ConfirmOrderStatusEnum;
import com.train.mapper.ConfirmOrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Mr.Liu
 * @email yxml2580@163.com
 * @createDate 2023-05-16 18:54:37
 */

@Service
public class ConfirmOrderService {

    @Autowired
    private ConfirmOrderMapper mapper;

    public void save(ConfirmOrderSaveReq bean) {

        ConfirmOrder confirmOrder = BeanUtil.copyProperties(bean, ConfirmOrder.class);

        // 当前时间
        DateTime now = DateTime.now();

        if (ObjectUtil.isNull(confirmOrder.getId())) {
            // 新增
            //使用雪花算法生成id
            confirmOrder.setId(SnowUtil.getSnowflakeNextId());
            confirmOrder.setCreateTime(now);
            confirmOrder.setUpdateTime(now);

            mapper.insert(confirmOrder);
        } else {
            confirmOrder.setUpdateTime(now);
            mapper.updateByPrimaryKeySelective(confirmOrder);
        }

    }

    public DBPages<ConfirmOrderQueryRes> queryList(ConfirmOrderQueryReq bean) {

        ConfirmOrderExample example = new ConfirmOrderExample();
        ConfirmOrderExample.Criteria criteria = example.createCriteria();

        // 分页
        PageHelper.startPage(bean.getPageNum(), bean.getPageSize());

        List<ConfirmOrder> list = mapper.selectByExample(example);

        List<ConfirmOrderQueryRes> result = BeanUtil.copyToList(list, ConfirmOrderQueryRes.class);

        return DBPages.restPage(result);
    }

    public void delete(Long id) {
        mapper.deleteByPrimaryKey(id);
    }

    /**
     * 确认订单
     *
     * @param bean
     */
    public void doConfirm(ConfirmOrderSaveReq bean) {
        //省略业务数据校验，如：车次是否存在，余票是否存在，车次是否在有效期内，tickets条数>0，同乘客同车次是否已买过

        // 保存确认订单表，状态初始
        DateTime now = DateTime.now();
        ConfirmOrder confirmOrder = new ConfirmOrder();
        confirmOrder.setId(SnowUtil.getSnowflakeNextId());
        confirmOrder.setCreateTime(now);
        confirmOrder.setUpdateTime(now);
        confirmOrder.setMemberId(LoginAccountContext.getId());
        confirmOrder.setDate(bean.getDate());
        confirmOrder.setTrainCode(bean.getTrainCode());
        confirmOrder.setStart(bean.getStart());
        confirmOrder.setEnd(bean.getEnd());
        confirmOrder.setDailyTrainTicketId(bean.getDailyTrainTicketId());
        confirmOrder.setStatus(ConfirmOrderStatusEnum.INIT.getCode());
        confirmOrder.setTickets(JSON.toJSONString(bean.getTickets()));
        mapper.insert(confirmOrder);

    }
}
