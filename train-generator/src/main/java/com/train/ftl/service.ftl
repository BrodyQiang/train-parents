package com.train.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.train.bean.request.${Domain}QueryReq;
import com.train.bean.request.${Domain}SaveReq;
import com.train.bean.response.${Domain}QueryRes;
import com.train.common.response.DBPages;
import com.train.common.util.SnowUtil;
import com.train.domain.${Domain};
import com.train.domain.${Domain}Example;
import com.train.mapper.${Domain}Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Mr.Liu
 * @email yxml2580@163.com
 * @createDate ${now?string("yyyy-MM-dd HH:mm:ss")}
 */

@Service
public class ${Domain}Service {

    @Autowired
    private ${Domain}Mapper mapper;

    public void save(${Domain}SaveReq bean) {

        ${Domain} ${domain} = BeanUtil.copyProperties(bean, ${Domain}.class);

        // 当前时间
        DateTime now = DateTime.now();

        if (ObjectUtil.isNull(${domain}.getId())) {
            // 新增
            //使用雪花算法生成id
            ${domain}.setId(SnowUtil.getSnowflakeNextId());
            ${domain}.setCreateTime(now);
            ${domain}.setUpdateTime(now);

            mapper.insert(${domain});
        } else {
            ${domain}.setUpdateTime(now);
            mapper.updateByPrimaryKeySelective(${domain});
        }

    }

    public DBPages<${Domain}QueryRes> queryList(${Domain}QueryReq bean) {

        ${Domain}Example example = new ${Domain}Example();
        ${Domain}Example.Criteria criteria = example.createCriteria();

        // 分页
        PageHelper.startPage(bean.getPageNum(), bean.getPageSize());

        List<${Domain}> list = mapper.selectByExample(example);

        List<${Domain}QueryRes> result = BeanUtil.copyToList(list, ${Domain}QueryRes.class);

        return DBPages.restPage(result);
    }

    public void delete(Long id) {
        mapper.deleteByPrimaryKey(id);
    }
}
