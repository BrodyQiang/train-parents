package com.train.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.ObjectUtil;
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
import com.train.domain.ConfirmOrder;
import com.train.domain.ConfirmOrderExample;
import com.train.domain.DailyTrainTicket;
import com.train.enums.ConfirmOrderStatusEnum;
import com.train.enums.SeatTypeEnum;
import com.train.mapper.ConfirmOrderMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
