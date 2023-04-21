package com.train.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import com.train.common.exception.BusinessException;
import com.train.common.exception.BusinessExceptionEnum;
import com.train.common.util.SnowUtil;
import com.train.domain.AccountInfo;
import com.train.domain.AccountInfoExample;
import com.train.mapper.AccountInfoMapper;
import com.train.request.AccountRegisterReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Mr.Liu
 * @email yxml2580@163.com
 * @createDate 2023/4/21 20:34
 */
@Service
public class AccountService {

    @Autowired
    private AccountInfoMapper accountInfoMapper;

    public long count() {
        return accountInfoMapper.countByExample(null);
    }

    public long register(AccountRegisterReq bean) {
        //先去数据库查询是否存在
        AccountInfoExample example = new AccountInfoExample();
        //创建where条件 并且手机号是否存在
        example.createCriteria().andMobileEqualTo(bean.getMobile());
        //去数据库查询
        List<AccountInfo> result = accountInfoMapper.selectByExample(example);

        if (CollUtil.isNotEmpty(result)) {
            //return result.get(0).getId();
            //如果存在
            throw new BusinessException(BusinessExceptionEnum.MEMBER_MOBILE_EXIST);
        }


        AccountInfo info = new AccountInfo();

        // todo 先用时间戳代替
        info.setId(SnowUtil.getSnowflakeNextId());
        info.setMobile(bean.getMobile());

        accountInfoMapper.insert(info);
        return info.getId();
    }

}
