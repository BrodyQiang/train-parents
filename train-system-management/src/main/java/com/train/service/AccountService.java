package com.train.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import com.train.common.exception.BusinessException;
import com.train.common.enums.BusinessExceptionEnum;
import com.train.common.util.JwtUtil;
import com.train.common.util.SnowUtil;
import com.train.domain.AccountInfo;
import com.train.domain.AccountInfoExample;
import com.train.domain.SmsRecord;
import com.train.domain.SmsRecordExample;
import com.train.mapper.AccountInfoMapper;
import com.train.mapper.SmsRecordMapper;
import com.train.bean.request.AccountLoginReq;
import com.train.bean.request.AccountRegisterReq;
import com.train.bean.request.AccountSendCodeReq;
import com.train.bean.response.AccountLoginRes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Mr.Liu
 * @email yxml2580@163.com
 * @createDate 2023/4/21 20:34
 */
@Service
@Slf4j
public class AccountService {

    @Autowired
    private AccountInfoMapper accountInfoMapper;

    @Autowired
    private SmsRecordMapper smsRecordMapper;

    public long count() {
        return accountInfoMapper.countByExample(null);
    }

    public long register(AccountRegisterReq bean) {
        String mobile = bean.getMobile();
        //先去数据库查询是否存在
        AccountInfo result = getAccountInfo(mobile);

        if (ObjectUtil.isNotNull(result)) {
            //return result.get(0).getId();
            //如果存在
            throw new BusinessException(BusinessExceptionEnum.MEMBER_MOBILE_EXIST);
        }


        AccountInfo info = new AccountInfo();

        // 使用的是雪花算法
        info.setId(SnowUtil.getSnowflakeNextId());
        info.setMobile(mobile);

        accountInfoMapper.insert(info);
        return info.getId();
    }

    public String sendCode(AccountSendCodeReq bean) {

        String mobile = bean.getMobile();

        //先去数据库查询是否存在
        AccountInfo result = getAccountInfo(mobile);

        // 在数据库中查询是否存在 不如果存在就在数据库中插入一条数据
        if (ObjectUtil.isNull(result)) {
            log.info("手机号不存在，插入一条数据");
            AccountInfo info = new AccountInfo();
            // 使用的是雪花算法
            info.setId(SnowUtil.getSnowflakeNextId());
            info.setMobile(mobile);
            accountInfoMapper.insert(info);
        } else {
            log.info("手机号存在，不需要插入数据");
        }

        String code;

        // 判断短信信息表中是否存在该手机号的记录
        SmsRecordExample smsRecordExample = new SmsRecordExample();
        smsRecordExample.createCriteria().andMobileEqualTo(mobile);
        List<SmsRecord> smsRecords = smsRecordMapper.selectByExample(smsRecordExample);

        // 如果不存在 进行插入
        if (CollUtil.isEmpty(smsRecords)) {

            // 生成短信验证码
            code = RandomUtil.randomString(4);
            log.info("生成的短信验证码为：{}", code);
            DateTime date = DateTime.now();

            // 保存短息记录表 表中字段应该有：手机号、验证码、发送时间、过期时间、发送状态、发送类型  --->手机号，短信验证码，有效期，是否已使用，业务类型，发送时间，使用时间
            SmsRecord smsRecord = new SmsRecord();
            smsRecord.setId(SnowUtil.getSnowflakeNextId());
            smsRecord.setMobile(mobile);
            smsRecord.setCode(code);
            smsRecord.setSendTime(date);
            smsRecord.setExpireTime(date);
            smsRecord.setSendStatus("1");
            smsRecord.setSendType("1");
            smsRecordMapper.insert(smsRecord);

            log.info("保存短信记录表");

            // 对接短信通道，发送短信
            log.info("对接短信通道");
        }else {
            // 如果存在，就从数据库中查询出来
            SmsRecord smsRecord = smsRecords.get(0);
            code = smsRecord.getCode();
        }
        return code;

    }

    public AccountLoginRes login(AccountLoginReq bean) {
        String mobile = bean.getMobile();

        //从数据库中查询是否存在
        AccountInfo result = getAccountInfo(mobile);

        // 在数据库中查询是否存在 不如果存在就在数据库中插入一条数据
        if (ObjectUtil.isNull(result)) {
            throw new BusinessException(BusinessExceptionEnum.MEMBER_MOBILE_NOT_EXIST);
        }

        // 从数据库中查询短信验证码
        SmsRecordExample example = new SmsRecordExample();
        example.createCriteria().andMobileEqualTo(mobile);
        List<SmsRecord> smsRecords = smsRecordMapper.selectByExample(example);
        if (CollUtil.isEmpty(smsRecords)) {
            throw new BusinessException(BusinessExceptionEnum.MEMBER_MOBILE_NOT_EXIST);
        }
        String code = smsRecords.get(0).getCode();

        // 校验短信验证码
        if (!code.equals(bean.getCode())) {
            throw new BusinessException(BusinessExceptionEnum.MEMBER_MOBILE_CODE_ERROR);
        }

        AccountLoginRes res = BeanUtil.copyProperties(result, AccountLoginRes.class);
        res.setAccessToken(JwtUtil.createToken(result.getId(), result.getMobile()));
        return res;

    }

    private AccountInfo getAccountInfo(String mobile) {
        //先去数据库查询是否存在
        AccountInfoExample example = new AccountInfoExample();
        //创建where条件 并且手机号是否存在
        example.createCriteria().andMobileEqualTo(mobile);
        //去数据库查询
        List<AccountInfo> result = accountInfoMapper.selectByExample(example);
        if (CollUtil.isEmpty(result)) {
            return null;
        }
        return result.get(0);
    }

}
