package com.train.service;

import com.train.domain.DailyTrainSeat;
import com.train.mapper.DailyTrainSeatMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author Mr.Liu
 * @email yxml2580@163.com
 * @createDate 2023-05-16 18:54:37
 */

@Service
public class AfterConfirmOrderService {

    private static final Logger LOG = LoggerFactory.getLogger(DailyTrainCarriageService.class);

    @Autowired
    private DailyTrainSeatMapper mapper;

    /**
     * @author Mr.Liu
     * @date 2023/5/23 19:30
     * 选中座位后事务处理：
     *   座位表修改售卖情况sell；
     *   余票详情表修改余票；
     *   为会员增加购票记录
     *   更新确认订单为成功
     * @param finalSeatList 最终的座位列表
     */
    @Transactional
    public void afterDoConfirm(List<DailyTrainSeat> finalSeatList) {
        finalSeatList.forEach(pp -> {
            // 创建新的座位对象 做shell的修改
            DailyTrainSeat newSeat = new DailyTrainSeat();
            newSeat.setId(pp.getId());
            newSeat.setSell(pp.getSell());
            newSeat.setUpdateTime(new Date());
            // 修改座位表
            mapper.updateByPrimaryKeySelective(newSeat);
        });

    }
}
