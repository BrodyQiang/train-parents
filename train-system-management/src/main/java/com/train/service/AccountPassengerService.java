package com.train.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.train.bean.request.AccountPassengerQueryReq;
import com.train.bean.request.AccountPassengerSaveReq;
import com.train.bean.response.AccountPassengerQueryRes;
import com.train.common.context.LoginAccountContext;
import com.train.common.response.DBPages;
import com.train.common.response.DBResult;
import com.train.common.util.SnowUtil;
import com.train.domain.AccountPassenger;
import com.train.domain.AccountPassengerExample;
import com.train.mapper.AccountPassengerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Mr.Liu
 * @email yxml2580@163.com
 * @createDate 2023-05-02 15:15:44
 */

@Service
public class AccountPassengerService {

    @Autowired
    private AccountPassengerMapper mapper;

    public void save(AccountPassengerSaveReq bean) {

        AccountPassenger accountPassenger = BeanUtil.copyProperties(bean, AccountPassenger.class);

        // 当前时间
        DateTime now = DateTime.now();

        if (ObjectUtil.isNull(accountPassenger.getId())) {
            // 新增
            //使用雪花算法生成id
            accountPassenger.setId(SnowUtil.getSnowflakeNextId());
            accountPassenger.setCreateTime(now);
            accountPassenger.setUpdateTime(now);

            mapper.insert(accountPassenger);
        } else {
            accountPassenger.setUpdateTime(now);
            mapper.updateByPrimaryKeySelective(accountPassenger);
        }

    }

    public DBPages<AccountPassengerQueryRes> queryList(AccountPassengerQueryReq bean) {

        AccountPassengerExample example = new AccountPassengerExample();
        AccountPassengerExample.Criteria criteria = example.createCriteria();

        if (ObjectUtil.isNotNull(bean.getMemberId())) {
            criteria.andMemberIdEqualTo(bean.getMemberId());
        }
        // 分页
        PageHelper.startPage(bean.getPageNum(), bean.getPageSize());

        List<AccountPassenger> list = mapper.selectByExample(example);

        List<AccountPassengerQueryRes> result = BeanUtil.copyToList(list, AccountPassengerQueryRes.class);

        return DBPages.restPage(result);
    }

    public void delete(Long id) {
        mapper.deleteByPrimaryKey(id);
    }

    /***
     * @author Mr.Liu
     * @date 2023/5/15 19:49
     * @return List<AccountPassengerQueryRes> 获取当前登录用户的乘客信息
     */
    public List<AccountPassengerQueryRes> queryMine() {
        AccountPassengerExample example = new AccountPassengerExample();
        example.setOrderByClause("name desc");
        // 当前登录用户
        example.createCriteria().andMemberIdEqualTo(LoginAccountContext.getId());
        List<AccountPassenger> list = mapper.selectByExample(example);
        return BeanUtil.copyToList(list, AccountPassengerQueryRes.class);
    }
}
