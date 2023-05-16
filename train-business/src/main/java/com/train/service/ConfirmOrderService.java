package com.train.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.train.bean.request.ConfirmOrderQueryReq;
import com.train.bean.request.ConfirmOrderSaveReq;
import com.train.bean.response.ConfirmOrderQueryRes;
import com.train.common.response.DBPages;
import com.train.common.util.SnowUtil;
import com.train.domain.ConfirmOrder;
import com.train.domain.ConfirmOrderExample;
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

    public void doConfirm(ConfirmOrderSaveReq bean) {

    }
}
