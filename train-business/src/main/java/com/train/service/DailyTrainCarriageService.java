package com.train.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.train.bean.request.DailyTrainCarriageQueryReq;
import com.train.bean.request.DailyTrainCarriageSaveReq;
import com.train.bean.response.DailyTrainCarriageQueryRes;
import com.train.common.response.DBPages;
import com.train.common.util.SnowUtil;
import com.train.domain.DailyTrainCarriage;
import com.train.domain.DailyTrainCarriageExample;
import com.train.enums.SeatColEnum;
import com.train.mapper.DailyTrainCarriageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Mr.Liu
 * @email yxml2580@163.com
 * @createDate 2023-05-08 18:11:12
 */

@Service
public class DailyTrainCarriageService {

    @Autowired
    private DailyTrainCarriageMapper mapper;

    public void save(DailyTrainCarriageSaveReq bean) {


        // 当前时间
        DateTime now = DateTime.now();

        // 自动计算出列数和总座位数
        List<SeatColEnum> seatColEnums = SeatColEnum.getColsByType(bean.getSeatType());
        bean.setColCount(seatColEnums.size());
        bean.setSeatCount(bean.getColCount() * bean.getRowCount());


        DailyTrainCarriage dailyTrainCarriage = BeanUtil.copyProperties(bean, DailyTrainCarriage.class);
        if (ObjectUtil.isNull(dailyTrainCarriage.getId())) {
            // 新增
            //使用雪花算法生成id
            dailyTrainCarriage.setId(SnowUtil.getSnowflakeNextId());
            dailyTrainCarriage.setCreateTime(now);
            dailyTrainCarriage.setUpdateTime(now);

            mapper.insert(dailyTrainCarriage);
        } else {
            dailyTrainCarriage.setUpdateTime(now);
            mapper.updateByPrimaryKeySelective(dailyTrainCarriage);
        }

    }

    public DBPages<DailyTrainCarriageQueryRes> queryList(DailyTrainCarriageQueryReq bean) {

        DailyTrainCarriageExample example = new DailyTrainCarriageExample();
        example.setOrderByClause("date desc, train_code asc,`index` asc");
        DailyTrainCarriageExample.Criteria criteria = example.createCriteria();
        // 查询条件 选择查询那天的车次
        if (ObjectUtil.isNotNull(bean.getDate())) {
            criteria.andDateEqualTo(bean.getDate());
        }
        // 查询条件 选择查询那天的车次
        if (ObjectUtil.isNotEmpty(bean.getTrainCode())) {
            criteria.andTrainCodeEqualTo(bean.getTrainCode());
        }
        // 分页
        PageHelper.startPage(bean.getPageNum(), bean.getPageSize());

        List<DailyTrainCarriage> list = mapper.selectByExample(example);

        List<DailyTrainCarriageQueryRes> result = BeanUtil.copyToList(list, DailyTrainCarriageQueryRes.class);

        return DBPages.restPage(result);
    }

    public void delete(Long id) {
        mapper.deleteByPrimaryKey(id);
    }
}
