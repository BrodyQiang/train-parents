package com.train.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.train.bean.request.SkTokenQueryReq;
import com.train.bean.request.SkTokenSaveReq;
import com.train.bean.response.SkTokenQueryRes;
import com.train.common.response.DBPages;
import com.train.common.util.SnowUtil;
import com.train.domain.SkToken;
import com.train.domain.SkTokenExample;
import com.train.mapper.SkTokenMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Mr.Liu
 * @email yxml2580@163.com
 * @createDate 2023-06-10 17:41:31
 */

@Service
public class SkTokenService {

    @Autowired
    private SkTokenMapper mapper;

    public void save(SkTokenSaveReq bean) {

        SkToken skToken = BeanUtil.copyProperties(bean, SkToken.class);

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

    public DBPages<SkTokenQueryRes> queryList(SkTokenQueryReq bean) {

        SkTokenExample example = new SkTokenExample();
        SkTokenExample.Criteria criteria = example.createCriteria();

        // 分页
        PageHelper.startPage(bean.getPageNum(), bean.getPageSize());

        List<SkToken> list = mapper.selectByExample(example);

        List<SkTokenQueryRes> result = BeanUtil.copyToList(list, SkTokenQueryRes.class);

        return DBPages.restPage(result);
    }

    public void delete(Long id) {
        mapper.deleteByPrimaryKey(id);
    }
}
