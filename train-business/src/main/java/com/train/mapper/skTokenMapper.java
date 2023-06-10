package com.train.mapper;

import com.train.domain.skToken;
import com.train.domain.skTokenExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface skTokenMapper {
    long countByExample(skTokenExample example);

    int deleteByExample(skTokenExample example);

    int deleteByPrimaryKey(Long id);

    int insert(skToken record);

    int insertSelective(skToken record);

    List<skToken> selectByExample(skTokenExample example);

    skToken selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") skToken record, @Param("example") skTokenExample example);

    int updateByExample(@Param("record") skToken record, @Param("example") skTokenExample example);

    int updateByPrimaryKeySelective(skToken record);

    int updateByPrimaryKey(skToken record);
}