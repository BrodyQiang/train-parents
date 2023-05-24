package com.train.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.NumberUtil;
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

    @Autowired
    private AfterConfirmOrderService afterConfirmOrderService;


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
        confirmOrder.setTickets(JSON.toJSONString(tickets));
        mapper.insert(confirmOrder);

        // 查出余票记录，需要得到真实的库存
        DailyTrainTicket dailyTrainTicket = dailyTrainTicketService.selectByUnique(date, trainCode, start, end);
        LOG.info("查出余票记录：{}", dailyTrainTicket);

        // 预扣减余票数量，并判断余票是否足够
        reduceTickets(bean, dailyTrainTicket);

        // 最终的选座结果
        List<DailyTrainSeat> finalSeatList = new ArrayList<>();

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
            getSeat(finalSeatList,date,trainCode,oneTicket.getSeatTypeCode(),oneTicket.getSeat().split("")[0], // 从A1得到A
                    offsetList,
                    dailyTrainTicket.getStartIndex(),
                    dailyTrainTicket.getEndIndex()
            );

        } else {

            LOG.info("本次购票没有选座");
            // 没有选座，就是随机分配座位
            for (ConfirmOrderTicketReq ticketReq : tickets) {
                // 随机分配座位  一个车箱一个车箱的获取座位数据
                getSeat(finalSeatList,date,trainCode,ticketReq.getSeatTypeCode(),null,null,dailyTrainTicket.getStartIndex(),dailyTrainTicket.getEndIndex());
            }
        }

        LOG.info("最终选座：{}", finalSeatList);

        // 保存座位信息 由于事务的原因 要让事务生效 所以写在别的类里面
        afterConfirmOrderService.afterDoConfirm(dailyTrainTicket,finalSeatList);


    }

    /**
     * 获取座位
     * 挑座位，如果有选座，则一次性挑完，如果无选座，则一个一个挑
     * @param finalSeatList 最终的选座结果
     * @param date 日期
     * @param trainCode 车次
     * @param seatType 座位类型
     * @param column 第一个座位所在的列
     * @param offsetList 相对第一个座位的偏移值
     * @param startIndex 开始下标 开始站序
     * @param endIndex 结束下标 结束站序
     */
    private void getSeat(List<DailyTrainSeat> finalSeatList,Date date, String trainCode, String seatType, String column, List<Integer> offsetList, Integer startIndex, Integer endIndex) {

        // 本次购票的座位列表 放在临时变量里
        List<DailyTrainSeat> getSeatList = new ArrayList<>();

        // 查出符合条件的车厢
        List<DailyTrainCarriage> carriageList = dailyTrainCarriageService.selectBySeatType(date, trainCode, seatType);
        LOG.info("共查出{}个符合条件的车厢", carriageList.size());

        // 一个车箱一个车箱的获取座位数据
        for (DailyTrainCarriage dailyTrainCarriage : carriageList) {
            LOG.info("开始从车厢{}选座", dailyTrainCarriage.getIndex());
            getSeatList = new ArrayList<>(); // 清空本次购票所在车厢的座位列表
            // 查出车厢的座位数
            List<DailyTrainSeat> seatList = dailyTrainSeatService.selectByCarriage(date, trainCode, dailyTrainCarriage.getIndex());
            LOG.info("车厢{}的座位数：{}", dailyTrainCarriage.getIndex(), seatList.size());

            for (DailyTrainSeat dailyTrainSeat : seatList) {
                String col = dailyTrainSeat.getCol(); // 座位所在的列
                Integer seatIndex = dailyTrainSeat.getCarriageSeatIndex(); // 座位在车厢中的下标

                // 判断当前座位不能被选中过
                boolean alreadyChooseFlag = false;
                for (DailyTrainSeat finalSeat : finalSeatList){

                    if (finalSeat.getId().equals(dailyTrainSeat.getId())) {
                        alreadyChooseFlag = true;
                        break;
                    }
                }
                if (alreadyChooseFlag) {
                    LOG.info("座位{}被选中过，不能重复选中，继续判断下一个座位", seatIndex);
                    continue;
                }

                // 判断column是否为空 为空则不进行选座
                if (StrUtil.isBlank(column)){
                    LOG.info("本次购票没有选座");
                } else {
                    if (!col.equals(column)) {
                        LOG.info("座位{}列值不对，继续判断下一个座位，当前列值：{}，目标列值：{}", seatIndex, col, column);
                        continue;
                    }
                }

                // 计算本座位是否可卖
                boolean isChoose = calSell(dailyTrainSeat, startIndex, endIndex);
                if (isChoose) {
                    LOG.info("选中座位");
                    getSeatList.add(dailyTrainSeat);
                } else {
                    continue;
                }

                // 跳出最外层循环，说明选座不成功
                boolean isGetAllOffsetSeat = true;
                // 根据offsetList偏移值，计算出本座位的座位号
                if (CollUtil.isNotEmpty(offsetList)) {
                    LOG.info("有偏移值：{}，校验偏移的座位是否可选", offsetList);
                    // 从索引1开始，索引0就是当前已选中的票
                    for (int i = 1; i < offsetList.size(); i++) {
                        Integer offset = offsetList.get(i);
                        // 计算出偏移后的座位号 由于座位在库的索引是从1开始 所以这里要-1
                        Integer nextIndex = seatIndex + offset -1;
                        // 有选座时，一定是在同一个车箱
                        if (nextIndex >= seatList.size()) {
                            LOG.info("座位{}不可选，偏移后的索引超出了这个车箱的座位数", nextIndex);
                            isGetAllOffsetSeat = false;
                            break;
                        }
                        // 获取偏移后的座位
                        DailyTrainSeat nextDailyTrainSeat = seatList.get(nextIndex);
                        // 校验偏移后的座位是否可选
                        boolean nextIsChoose = calSell(nextDailyTrainSeat, startIndex, endIndex);
                        if (nextIsChoose) {
                            LOG.info("座位{}被选中", nextDailyTrainSeat.getCarriageSeatIndex());
                            getSeatList.add(nextDailyTrainSeat);
                        } else {
                            LOG.info("座位{}不可选", nextDailyTrainSeat.getCarriageSeatIndex());
                            isGetAllOffsetSeat = false;
                            break;
                        }
                    }
                }
                // 如果有选座，但是偏移后的座位不可选，则跳过本次循环
                if (!isGetAllOffsetSeat) {
                    LOG.info("偏移后的座位不可选，跳过本次循环");
                    getSeatList = new ArrayList<>(); // 清空本次购票所在车厢的座位列表
                    continue;
                }

                // 保存选好的座位 选座成功 将本次选座的座位列表加入到最终的座位列表中
                finalSeatList.addAll(getSeatList);
                return;
            }
        }
    }

    /**
     * 计算某座位在区间内是否可卖
     * 例：sell=10001，本次购买区间站1~4，则区间已售000
     * 全部是0，表示这个区间可买；只要有1，就表示区间内已售过票
     *
     * 选中后，要计算购票后的sell，比如原来是10001，本次购买区间站1~4
     * 方案：构造本次购票造成的售卖信息01110，和原sell 10001按位与，最终得到11111
     */
    private boolean calSell(DailyTrainSeat dailyTrainSeat, Integer startIndex, Integer endIndex) {
        // 00001, 00000
        String sell = dailyTrainSeat.getSell();
        //  000, 000
        String sellPart = sell.substring(startIndex, endIndex);
        if (Integer.parseInt(sellPart) > 0) {
            LOG.info("座位{}在本次车站区间{}~{}已售过票，不可选中该座位", dailyTrainSeat.getCarriageSeatIndex(), startIndex, endIndex);
            return false;
        } else {
            LOG.info("座位{}在本次车站区间{}~{}未售过票，可选中该座位", dailyTrainSeat.getCarriageSeatIndex(), startIndex, endIndex);
            //  111,   111
            String curSell = sellPart.replace('0', '1');
            // 0111,  0111
            curSell = StrUtil.fillBefore(curSell, '0', endIndex);
            // 01110, 01110
            curSell = StrUtil.fillAfter(curSell, '0', sell.length());

            // 当前区间售票信息curSell 01110与库里的已售信息sell 00001按位与，即可得到该座位卖出此票后的售票详情
            // 15(01111), 14(01110 = 01110|00000)
            int newSellInt = NumberUtil.binaryToInt(curSell) | NumberUtil.binaryToInt(sell);
            //  1111,  1110
            String newSell = NumberUtil.getBinaryStr(newSellInt);
            // 01111, 01110
            newSell = StrUtil.fillBefore(newSell, '0', sell.length());
            LOG.info("座位{}被选中，原售票信息：{}，车站区间：{}~{}，即：{}，最终售票信息：{}"
                    , dailyTrainSeat.getCarriageSeatIndex(), sell, startIndex, endIndex, curSell, newSell);
            dailyTrainSeat.setSell(newSell);
            return true;

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
