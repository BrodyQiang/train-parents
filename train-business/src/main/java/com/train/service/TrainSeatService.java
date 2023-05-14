package com.train.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.train.bean.request.TrainSeatQueryReq;
import com.train.bean.request.TrainSeatSaveReq;
import com.train.bean.response.TrainSeatQueryRes;
import com.train.common.response.DBPages;
import com.train.common.util.SnowUtil;
import com.train.domain.TrainCarriage;
import com.train.domain.TrainSeat;
import com.train.domain.TrainSeatExample;
import com.train.enums.SeatColEnum;
import com.train.mapper.TrainSeatMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Mr.Liu
 * @email yxml2580@163.com
 * @createDate 2023-05-05 18:40:05
 */

@Service
public class TrainSeatService {

    @Autowired
    private TrainSeatMapper mapper;

    @Autowired
    private TrainCarriageService service;

    public void save(TrainSeatSaveReq bean) {

        TrainSeat trainSeat = BeanUtil.copyProperties(bean, TrainSeat.class);

        // 当前时间
        DateTime now = DateTime.now();

        if (ObjectUtil.isNull(trainSeat.getId())) {
            // 新增
            //使用雪花算法生成id
            trainSeat.setId(SnowUtil.getSnowflakeNextId());
            trainSeat.setCreateTime(now);
            trainSeat.setUpdateTime(now);

            mapper.insert(trainSeat);
        } else {
            trainSeat.setUpdateTime(now);
            mapper.updateByPrimaryKeySelective(trainSeat);
        }

    }

    public DBPages<TrainSeatQueryRes> queryList(TrainSeatQueryReq bean) {

        TrainSeatExample example = new TrainSeatExample();
        example.setOrderByClause("train_code asc,carriage_index asc,carriage_seat_index asc");
        TrainSeatExample.Criteria criteria = example.createCriteria();
        // 添加查询条件 按照车次查询
        if (ObjectUtil.isNotEmpty(bean.getTrainCode())) {
            criteria.andTrainCodeEqualTo(bean.getTrainCode());
        }

        // 分页
        PageHelper.startPage(bean.getPageNum(), bean.getPageSize());

        List<TrainSeat> list = mapper.selectByExample(example);

        List<TrainSeatQueryRes> result = BeanUtil.copyToList(list, TrainSeatQueryRes.class);

        return DBPages.restPage(result);
    }

    public void delete(Long id) {
        mapper.deleteByPrimaryKey(id);
    }


    @Transactional
    public void genTrainSeat(String trainCode) {
        DateTime now = DateTime.now();
        // 清空当前车次下的所有的座位记录
        TrainSeatExample trainSeatExample = new TrainSeatExample();
        TrainSeatExample.Criteria criteria = trainSeatExample.createCriteria();
        criteria.andTrainCodeEqualTo(trainCode);
        mapper.deleteByExample(trainSeatExample);

        // 查找当前车次下的所有的车厢
        List<TrainCarriage> carriageList = service.selectByTrainCode(trainCode);

        // 循环生成每个车厢的座位
        for (TrainCarriage trainCarriage : carriageList) {
            // 拿到车厢数据：行数、座位类型(得到列数)
            Integer rowCount = trainCarriage.getRowCount();
            String seatType = trainCarriage.getSeatType();
            int seatIndex = 1;

            // 根据车厢的座位类型，筛选出所有的列，比如车箱类型是一等座，则筛选出columnList={ACDF}
            List<SeatColEnum> colEnumList = SeatColEnum.getColsByType(seatType);

            // 循环行数
            for (int row = 1; row <= rowCount; row++) {
                // 循环列数
                for (SeatColEnum seatColEnum : colEnumList) {
                    // 构造座位数据并保存数据库
                    TrainSeat trainSeat = new TrainSeat();
                    trainSeat.setId(SnowUtil.getSnowflakeNextId());
                    trainSeat.setTrainCode(trainCode);
                    trainSeat.setCarriageIndex(trainCarriage.getIndex());
                    trainSeat.setRow(StrUtil.fillBefore(String.valueOf(row), '0', 2));
                    trainSeat.setCol(seatColEnum.getCode());
                    trainSeat.setSeatType(seatType);
                    trainSeat.setCarriageSeatIndex(seatIndex++);
                    trainSeat.setCreateTime(now);
                    trainSeat.setUpdateTime(now);
                    mapper.insert(trainSeat);
                }
            }
        }
    }

    /**
     * 根据车次查询所有的座位
     * @param trainCode
     * @return
     */
    public List<TrainSeat> selectByTrainCode(String trainCode) {
        TrainSeatExample example = new TrainSeatExample();
        example.setOrderByClause("carriage_index asc,carriage_seat_index asc");
        TrainSeatExample.Criteria criteria = example.createCriteria();
        criteria.andTrainCodeEqualTo(trainCode);
        return mapper.selectByExample(example);
    }
}


