package com.train.mapper;

import com.train.domain.AccountPassenger;
import com.train.domain.AccountPassengerExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface AccountPassengerMapper {
    long countByExample(AccountPassengerExample example);

    int deleteByExample(AccountPassengerExample example);

    int deleteByPrimaryKey(Long id);

    int insert(AccountPassenger record);

    int insertSelective(AccountPassenger record);

    List<AccountPassenger> selectByExample(AccountPassengerExample example);

    AccountPassenger selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") AccountPassenger record, @Param("example") AccountPassengerExample example);

    int updateByExample(@Param("record") AccountPassenger record, @Param("example") AccountPassengerExample example);

    int updateByPrimaryKeySelective(AccountPassenger record);

    int updateByPrimaryKey(AccountPassenger record);
}