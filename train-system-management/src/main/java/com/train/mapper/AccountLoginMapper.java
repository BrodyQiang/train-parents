package com.train.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.train.domain.entity.AccountInfoBean;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountLoginMapper extends BaseMapper<AccountInfoBean> {
}
