package com.train.service;

import com.train.domain.DailyTrainSeat;
import com.train.domain.DailyTrainTicket;
import com.train.mapper.DailyTrainSeatMapper;
import com.train.mapper.myMapper.MyMapperConfirmOrderMapper;
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

    @Autowired
    private MyMapperConfirmOrderMapper myMapperConfirmOrderMapper;

    /**
     * @param finalSeatList 最终的座位列表
     * @author Mr.Liu
     * @date 2023/5/23 19:30
     * 选中座位后事务处理：
     * 座位表修改售卖情况sell；
     * 余票详情表修改余票；
     * 为会员增加购票记录
     * 更新确认订单为成功
     */
    @Transactional
    public void afterDoConfirm(DailyTrainTicket dailyTrainTicket, List<DailyTrainSeat> finalSeatList) {
        finalSeatList.forEach(pp -> {
            // 创建新的座位对象 做shell的修改
            DailyTrainSeat newSeat = new DailyTrainSeat();
            newSeat.setId(pp.getId());
            newSeat.setSell(pp.getSell());
            newSeat.setUpdateTime(new Date());
            // 修改座位表
            mapper.updateByPrimaryKeySelective(newSeat);

            // 计算这个站卖出去后，影响了哪些站的余票库存
            // 参照2-3节 如何保证不超卖、不少卖，还要能承受极高的并发 10:30左右
            // 影响的库存：本次选座之前没卖过票的，和本次购买的区间有交集的区间
            // 假设10个站，本次买4~7站
            // 原售：001000001
            // 购买：000011100
            // 新售：001011101
            // 影响：XXX11111X
            // Integer startIndex = 4;
            // Integer endIndex = 7;
            // Integer minStartIndex = startIndex - 往前碰到的最后一个0;
            // Integer maxStartIndex = endIndex - 1;
            // Integer minEndIndex = startIndex + 1;
            // Integer maxEndIndex = endIndex + 往后碰到的最后一个0;
            Integer startIndex = dailyTrainTicket.getStartIndex();
            Integer endIndex = dailyTrainTicket.getEndIndex();
            char[] chars = newSeat.getSell().toCharArray();
            Integer maxStartIndex = endIndex - 1;
            Integer minEndIndex = startIndex + 1;
            Integer minStartIndex = 0;
            for (int i = startIndex - 1; i >= 0; i--) {
                char aChar = chars[i];
                if (aChar == '1') {
                    minStartIndex = i + 1;
                    break;
                }
            }
            LOG.info("影响出发站区间：" + minStartIndex + "-" + maxStartIndex);

            Integer maxEndIndex = newSeat.getSell().length();
            for (int i = endIndex; i < newSeat.getSell().length(); i++) {
                char aChar = chars[i];
                if (aChar == '1') {
                    maxEndIndex = i;
                    break;
                }
            }
            LOG.info("影响到达站区间：" + minEndIndex + "-" + maxEndIndex);

            // 修改余票详情表
            myMapperConfirmOrderMapper.updateCountBySell(
                    pp.getDate(),
                    pp.getTrainCode(),
                    pp.getSeatType(),
                    minStartIndex,
                    maxStartIndex,
                    minEndIndex,
                    maxEndIndex);
        });

    }
}
