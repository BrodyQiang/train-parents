package com.train.mapper;

import com.train.domain.AccountInfo;
import com.train.domain.AccountInfoExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface AccountInfoMapper {
    long countByExample(AccountInfoExample example);

    int deleteByExample(AccountInfoExample example);

    int deleteByPrimaryKey(Long id);

    int insert(AccountInfo record);

    int insertSelective(AccountInfo record);

    List<AccountInfo> selectByExample(AccountInfoExample example);

    AccountInfo selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") AccountInfo record, @Param("example") AccountInfoExample example);

    int updateByExample(@Param("record") AccountInfo record, @Param("example") AccountInfoExample example);

    int updateByPrimaryKeySelective(AccountInfo record);

    int updateByPrimaryKey(AccountInfo record);
}