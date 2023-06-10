package com.train.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.train.bean.request.skTokenQueryReq;
import com.train.bean.request.skTokenSaveReq;
import com.train.bean.response.skTokenQueryRes;
import com.train.common.response.DBPages;
import com.train.common.util.SnowUtil;
import com.train.domain.skToken;
import com.train.domain.skTokenExample;
import com.train.mapper.skTokenMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Mr.Liu
 * @email yxml2580@163.com
 * @createDate 2023-06-10 17:31:43
 */

@Service
public class skTokenService {

    @Autowired
    private skTokenMapper mapper;

    public void save(skTokenSaveReq bean) {

        skToken skToken = BeanUtil.copyProperties(bean, skToken.class);

        // 当前时间
        DateTime now = DateTime.now();

        if (ObjectUtil.isNull(skToken.getId())) {
            // 新增
            //使用雪花算法生成id
            skToken.setId(SnowUtil.getSnowflakeNextId());
            skToken.setCreateTime(now);
            skToken.setUpdateTime(now);

            mapper.insert(skToken);
        } else {
            skToken.setUpdateTime(now);
            mapper.updateByPrimaryKeySelective(skToken);
        }

    }

    public DBPages<skTokenQueryRes> queryList(skTokenQueryReq bean) {

        skTokenExample example = new skTokenExample();
        skTokenExample.Criteria criteria = example.createCriteria();

        // 分页
        PageHelper.startPage(bean.getPageNum(), bean.getPageSize());

        List<skToken> list = mapper.selectByExample(example);

        List<skTokenQueryRes> result = BeanUtil.copyToList(list, skTokenQueryRes.class);

        return DBPages.restPage(result);
    }

    public void delete(Long id) {
        mapper.deleteByPrimaryKey(id);
    }
}
