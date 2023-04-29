package com.train.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import com.train.bean.request.AccountPassengerSaveReq;
import com.train.common.context.LoginAccountContext;
import com.train.common.response.DBResult;
import com.train.common.util.SnowUtil;
import com.train.domain.AccountPassenger;
import com.train.mapper.AccountPassengerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Mr.Liu
 * @email yxml2580@163.com
 * @createDate 2023/4/29 13:17
 */
@Service
public class AccountPassengerService {

    @Autowired
    private AccountPassengerMapper mapper;

    public DBResult save(AccountPassengerSaveReq bean) {

        AccountPassenger accountPassenger = BeanUtil.copyProperties(bean, AccountPassenger.class);

        // 当前时间
        DateTime now = DateTime.now();

        //使用雪花算法生成id
        accountPassenger.setId(SnowUtil.getSnowflakeNextId());
        accountPassenger.setMemberId(LoginAccountContext.getId());
        accountPassenger.setCreateTime(now);
        accountPassenger.setUpdateTime(now);

        int result = mapper.insert(accountPassenger);

        return result > 0 ? DBResult.success() : DBResult.fail();

    }
}
