package com.train.service;

import com.train.bean.request.ConfirmOrderTicketReq;
import com.train.common.context.LoginAccountContext;
import com.train.common.feign.AccountFeignTicket;
import com.train.common.request.AccountTicketReq;
import com.train.common.response.DBResult;
import com.train.domain.ConfirmOrder;
import com.train.domain.DailyTrainSeat;
import com.train.domain.DailyTrainTicket;
import com.train.enums.ConfirmOrderStatusEnum;
import com.train.mapper.ConfirmOrderMapper;
import com.train.mapper.DailyTrainSeatMapper;
import com.train.mapper.myMapper.MyMapperConfirmOrderMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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

    @Autowired
    private AccountFeignTicket accountFeignTicket;

    @Autowired
    private ConfirmOrderMapper confirmOrderMapper;

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
//    @Transactional
//    @GlobalTransactional
    public void afterDoConfirm(DailyTrainTicket dailyTrainTicket, List<DailyTrainSeat> finalSeatList, List<ConfirmOrderTicketReq> tickets, ConfirmOrder confirmOrder) throws Exception {
        //LOG.info("seata全局事务ID: {}", RootContext.getXID());
        AtomicInteger index = new AtomicInteger(); // 用于记录当前下标
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
            int minStartIndex = 0;
            for (int i = startIndex - 1; i >= 0; i--) {
                char aChar = chars[i];
                if (aChar == '1') {
                    minStartIndex = i + 1;
                    break;
                }
            }
            LOG.info("影响出发站区间：" + minStartIndex + "-" + maxStartIndex);

            int maxEndIndex = newSeat.getSell().length();
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

            // 调用会员服务接口，为会员增加一张车票
            AccountTicketReq accountTicketReq = new AccountTicketReq();
            accountTicketReq.setMemberId(LoginAccountContext.getId());
            accountTicketReq.setPassengerId(tickets.get(index.get()).getPassengerId());
            accountTicketReq.setPassengerName(tickets.get(index.get()).getPassengerName());
            accountTicketReq.setTrainDate(pp.getDate());
            accountTicketReq.setTrainCode(pp.getTrainCode());
            accountTicketReq.setCarriageIndex(pp.getCarriageIndex());
            accountTicketReq.setSeatRow(pp.getRow());
            accountTicketReq.setSeatCol(pp.getCol());
            accountTicketReq.setStartStation(dailyTrainTicket.getStart());
            accountTicketReq.setStartTime(dailyTrainTicket.getStartTime());
            accountTicketReq.setEndStation(dailyTrainTicket.getEnd());
            accountTicketReq.setEndTime(dailyTrainTicket.getEndTime());
            accountTicketReq.setSeatType(pp.getSeatType());

            DBResult save = accountFeignTicket.save(accountTicketReq);
            LOG.info("调用会员服务接口，为会员增加一张车票{}：" + save);

            index.getAndIncrement();
            // 更新订单状态为成功
            ConfirmOrder confirmOrderForUpdate = new ConfirmOrder();
            confirmOrderForUpdate.setId(confirmOrder.getId());
            confirmOrderForUpdate.setUpdateTime(new Date());
            confirmOrderForUpdate.setStatus(ConfirmOrderStatusEnum.SUCCESS.getCode());
            confirmOrderMapper.updateByPrimaryKeySelective(confirmOrderForUpdate);

        });
        // 模拟调用方出现异常
        // Thread.sleep(10000);
//        if (1 == 1) {
//            throw new Exception("测试异常");
//        }

    }

}
