package com.train.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.train.bean.request.TrainQueryReq;
import com.train.bean.request.TrainSaveReq;
import com.train.bean.response.TrainQueryRes;
import com.train.common.enums.BusinessExceptionEnum;
import com.train.common.exception.BusinessException;
import com.train.common.response.DBPages;
import com.train.common.util.SnowUtil;
import com.train.domain.Train;
import com.train.domain.TrainExample;
import com.train.mapper.TrainMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Mr.Liu
 * @email yxml2580@163.com
 * @createDate 2023-05-05 18:32:50
 */

@Service
public class TrainService {

    @Autowired
    private TrainMapper mapper;

    public void save(TrainSaveReq bean) {

        Train train = BeanUtil.copyProperties(bean, Train.class);

        // 当前时间
        DateTime now = DateTime.now();

        if (ObjectUtil.isNull(train.getId())) {
            // 新增

            // 保存之前，先校验唯一键是否存在
            Train trainDB = selectByUnique(bean.getCode());
            if (ObjectUtil.isNotEmpty(trainDB)) {
                throw new BusinessException(BusinessExceptionEnum.BUSINESS_TRAIN_CODE_UNIQUE_ERROR);
            }

            //使用雪花算法生成id
            train.setId(SnowUtil.getSnowflakeNextId());
            train.setCreateTime(now);
            train.setUpdateTime(now);

            mapper.insert(train);
        } else {
            train.setUpdateTime(now);
            mapper.updateByPrimaryKeySelective(train);
        }

    }

    public DBPages<TrainQueryRes> queryList(TrainQueryReq bean) {

        TrainExample example = new TrainExample();
        example.setOrderByClause("id desc");
        TrainExample.Criteria criteria = example.createCriteria();

        // 分页
        PageHelper.startPage(bean.getPageNum(), bean.getPageSize());

        List<Train> list = mapper.selectByExample(example);

        List<TrainQueryRes> result = BeanUtil.copyToList(list, TrainQueryRes.class);

        return DBPages.restPage(result);
    }

    public void delete(Long id) {
        mapper.deleteByPrimaryKey(id);
    }

    /***
     * @author Mr.Liu
     * @date 2023/5/14 12:41
     * 查询所有的车次信息
     */
    public List<Train> selectAll() {
        TrainExample example = new TrainExample();
        example.setOrderByClause("code desc");
        return mapper.selectByExample(example);
    }

    private Train selectByUnique(String code) {
        TrainExample trainExample = new TrainExample();
        trainExample.createCriteria()
                .andCodeEqualTo(code);
        List<Train> list = mapper.selectByExample(trainExample);
        if (CollUtil.isNotEmpty(list)) {
            return list.get(0);
        } else {
            return null;
        }
    }

    /***
     * @author Mr.Liu
     * @date 2023/5/15 19:41
     * @return List<TrainQueryRes> 返回所有的车次信息
     */
    public List<TrainQueryRes> queryAll() {
        TrainExample example = new TrainExample();
        example.setOrderByClause("code desc");
        List<Train> list = mapper.selectByExample(example);
        return BeanUtil.copyToList(list, TrainQueryRes.class);
    }
}
