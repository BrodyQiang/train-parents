package com.train.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.train.bean.request.ConfirmOrderQueryReq;
import com.train.bean.request.ConfirmOrderSaveReq;
import com.train.bean.request.ConfirmOrderTicketReq;
import com.train.bean.response.ConfirmOrderQueryRes;
import com.train.common.context.LoginAccountContext;
import com.train.common.enums.BusinessExceptionEnum;
import com.train.common.exception.BusinessException;
import com.train.common.interceptor.AccountInterceptor;
import com.train.common.response.DBPages;
import com.train.common.util.SnowUtil;
import com.train.domain.*;
import com.train.enums.ConfirmOrderStatusEnum;
import com.train.enums.SeatColEnum;
import com.train.enums.SeatTypeEnum;
import com.train.mapper.ConfirmOrderMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Mr.Liu
 * @email yxml2580@163.com
 * @createDate 2023-05-16 18:54:37
 */

@Service
public class ConfirmOrderService {

    private static final Logger LOG = LoggerFactory.getLogger(DailyTrainCarriageService.class);

    @Autowired
    private ConfirmOrderMapper mapper;

    @Autowired
    private DailyTrainTicketService dailyTrainTicketService;

    @Autowired
    private DailyTrainCarriageService dailyTrainCarriageService;

    @Autowired
    private DailyTrainSeatService dailyTrainSeatService;


    public void save(ConfirmOrderSaveReq bean) {

        ConfirmOrder confirmOrder = BeanUtil.copyProperties(bean, ConfirmOrder.class);

        // 当前时间
        DateTime now = DateTime.now();

        if (ObjectUtil.isNull(confirmOrder.getId())) {
            // 新增
            //使用雪花算法生成id
            confirmOrder.setId(SnowUtil.getSnowflakeNextId());
            confirmOrder.setCreateTime(now);
            confirmOrder.setUpdateTime(now);

            mapper.insert(confirmOrder);
        } else {
            confirmOrder.setUpdateTime(now);
            mapper.updateByPrimaryKeySelective(confirmOrder);
        }

    }

    public DBPages<ConfirmOrderQueryRes> queryList(ConfirmOrderQueryReq bean) {

        ConfirmOrderExample example = new ConfirmOrderExample();
        ConfirmOrderExample.Criteria criteria = example.createCriteria();

        // 分页
        PageHelper.startPage(bean.getPageNum(), bean.getPageSize());

        List<ConfirmOrder> list = mapper.selectByExample(example);

        List<ConfirmOrderQueryRes> result = BeanUtil.copyToList(list, ConfirmOrderQueryRes.class);

        return DBPages.restPage(result);
    }

    public void delete(Long id) {
        mapper.deleteByPrimaryKey(id);
    }

    /**
     * 确认订单
     *
     * @param bean
     */
    public void doConfirm(ConfirmOrderSaveReq bean) {
        //省略业务数据校验，如：车次是否存在，余票是否存在，车次是否在有效期内，tickets条数>0，同乘客同车次是否已买过

        // 提取公共的参数
        Date date = bean.getDate();
        String trainCode = bean.getTrainCode();
        String start = bean.getStart();
        String end = bean.getEnd();
        List<ConfirmOrderTicketReq> tickets = bean.getTickets();

        // 保存确认订单表，状态初始
        DateTime now = DateTime.now();
        ConfirmOrder confirmOrder = new ConfirmOrder();
        confirmOrder.setId(SnowUtil.getSnowflakeNextId());
        confirmOrder.setCreateTime(now);
        confirmOrder.setUpdateTime(now);
        confirmOrder.setMemberId(LoginAccountContext.getId());
        confirmOrder.setDate(date);
        confirmOrder.setTrainCode(trainCode);
        confirmOrder.setStart(start);
        confirmOrder.setEnd(end);
        confirmOrder.setDailyTrainTicketId(bean.getDailyTrainTicketId());
        confirmOrder.setStatus(ConfirmOrderStatusEnum.INIT.getCode());
        confirmOrder.setTickets(JSON.toJSONString(bean.getTickets()));
        mapper.insert(confirmOrder);

        // 查出余票记录，需要得到真实的库存
        DailyTrainTicket dailyTrainTicket = dailyTrainTicketService.selectByUnique(date, trainCode, start, end);
        LOG.info("查出余票记录：{}", dailyTrainTicket);

        // 预扣减余票数量，并判断余票是否足够
        reduceTickets(bean, dailyTrainTicket);

        // 计算相对第一个座位的偏移值
        // 比如选择的是C1,D2，则偏移值是：[0,5]
        // 比如选择的是A1,B1,C1，则偏移值是：[0,1,2]
        ConfirmOrderTicketReq oneTicket = tickets.get(0);
        if(StrUtil.isNotBlank(oneTicket.getSeat())) {
            LOG.info("本次购票有选座");
            // 查出本次选座的座位类型都有哪些列，用于计算所选座位与第一个座位的偏离值
            List<SeatColEnum> colEnumList = SeatColEnum.getColsByType(oneTicket.getSeatTypeCode());
            LOG.info("本次选座的座位类型包含的列：{}", colEnumList);

            // 组成和前端两排选座一样的列表，用于作参照的座位列表，例：referSeatList = {A1, C1, D1, F1, A2, C2, D2, F2}
            List<String> referSeatList = new ArrayList<>();
            for (int i = 1; i <= 2; i++) {
                for (SeatColEnum seatColEnum : colEnumList) {
                    referSeatList.add(seatColEnum.getCode() + i);
                }
            }
            LOG.info("用于作参照的两排座位：{}", referSeatList);

            // 相对偏移值，即：相对第一个座位的偏移值
            List<Integer> offsetList = new ArrayList<>();

            // 绝对偏移值，即：在参照座位列表中的位置
            List<Integer> aboluteOffsetList = new ArrayList<>();
            for (ConfirmOrderTicketReq ticketReq : tickets) {
                int index = referSeatList.indexOf(ticketReq.getSeat());
                aboluteOffsetList.add(index);
            }
            LOG.info("计算得到所有座位的绝对偏移值：{}", aboluteOffsetList);
            for (Integer index : aboluteOffsetList) {
                int offset = index - aboluteOffsetList.get(0);
                offsetList.add(offset);
            }
            LOG.info("计算得到所有座位的相对第一个座位的偏移值：{}", offsetList);

            // 获取座位
            getSeat(date,trainCode,oneTicket.getSeatTypeCode(),oneTicket.getSeat().split("")[0], // 从A1得到A
                    offsetList
            );

        } else {

            LOG.info("本次购票没有选座");
            // 没有选座，就是随机分配座位
            for (ConfirmOrderTicketReq ticketReq : tickets) {
                // 随机分配座位  一个车箱一个车箱的获取座位数据
                getSeat(date,trainCode,ticketReq.getSeatTypeCode(),null,null);
            }
        }



    }

    /**
     * 获取座位
     *
     * @param date 日期
     * @param trainCode 车次
     * @param seatType 座位类型
     * @param column 第一个座位所在的列
     * @param offsetList 相对第一个座位的偏移值
     */
    private void getSeat(Date date, String trainCode, String seatType, String column, List<Integer> offsetList) {

        // 查出符合条件的车厢
        List<DailyTrainCarriage> carriageList = dailyTrainCarriageService.selectBySeatType(date, trainCode, seatType);
        LOG.info("共查出{}个符合条件的车厢", carriageList.size());

        // 一个车箱一个车箱的获取座位数据
        for (DailyTrainCarriage dailyTrainCarriage : carriageList) {
            LOG.info("开始从车厢{}选座", dailyTrainCarriage.getIndex());
            // 查出车厢的座位数
            List<DailyTrainSeat> seatList = dailyTrainSeatService.selectByCarriage(date, trainCode, dailyTrainCarriage.getIndex());
            LOG.info("车厢{}的座位数：{}", dailyTrainCarriage.getIndex(), seatList.size());
        }
    }

    /**
     * 预扣减余票数量，并判断余票是否足够
     *
     * @param bean
     * @param dailyTrainTicket
     */
    private void reduceTickets(ConfirmOrderSaveReq bean, DailyTrainTicket dailyTrainTicket) {
        for (ConfirmOrderTicketReq ticketReq : bean.getTickets()) {
            String seatTypeCode = ticketReq.getSeatTypeCode();
            SeatTypeEnum seatTypeEnum = EnumUtil.getBy(SeatTypeEnum::getCode, seatTypeCode);
            switch (seatTypeEnum) {
                case YDZ : {
                    // 一等座
                    int countLeft = dailyTrainTicket.getYdz() - 1; // 预扣减
                    if (countLeft < 0) {
                        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                    // 更新余票
                    dailyTrainTicket.setYdz(countLeft);
                    break;
                }
                case EDZ : {
                    // 二等座
                    int countLeft = dailyTrainTicket.getEdz() - 1;// 预扣减
                    if (countLeft < 0) {
                        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                    // 更新余票
                    dailyTrainTicket.setEdz(countLeft);
                    break;
                }
                case RW : {
                    // 软卧
                    int countLeft = dailyTrainTicket.getRw() - 1;// 预扣减
                    if (countLeft < 0) {
                        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                    // 更新余票
                    dailyTrainTicket.setRw(countLeft);
                    break;
                }
                case YW : {
                    // 硬卧
                    int countLeft = dailyTrainTicket.getYw() - 1;// 预扣减
                    if (countLeft < 0) {
                        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                    // 更新余票
                    dailyTrainTicket.setYw(countLeft);
                    break;
                }
            }
        }
    }
}
