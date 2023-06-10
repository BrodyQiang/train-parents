package com.train.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.train.bean.request.SkTokenQueryReq;
import com.train.bean.request.SkTokenSaveReq;
import com.train.bean.response.SkTokenQueryRes;
import com.train.common.response.DBPages;
import com.train.common.util.SnowUtil;
import com.train.domain.SkToken;
import com.train.domain.SkTokenExample;
import com.train.mapper.SkTokenMapper;
import com.train.mapper.myMapper.MyMapperSkTokenMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author Mr.Liu
 * @email yxml2580@163.com
 * @createDate 2023-06-10 17:41:31
 */

@Service
public class SkTokenService {

    private static final Logger LOG = LoggerFactory.getLogger(SkTokenService.class);

    @Autowired
    private SkTokenMapper mapper;

    @Autowired
    private DailyTrainSeatService dailyTrainSeatService;

    @Autowired
    private DailyTrainStationService dailyTrainStationService;

    @Autowired
    private MyMapperSkTokenMapper skTokenMapper;


    /**
     * 初始化
     */
    public void genDaily(Date date, String trainCode) {
        LOG.info("删除日期【{}】车次【{}】的令牌记录", DateUtil.formatDate(date), trainCode);
        SkTokenExample skTokenExample = new SkTokenExample();
        skTokenExample.createCriteria().andDateEqualTo(date).andTrainCodeEqualTo(trainCode);
        mapper.deleteByExample(skTokenExample);

        DateTime now = DateTime.now();
        SkToken skToken = new SkToken();
        skToken.setDate(date);
        skToken.setTrainCode(trainCode);
        skToken.setId(SnowUtil.getSnowflakeNextId());
        skToken.setCreateTime(now);
        skToken.setUpdateTime(now);

        int seatCount = dailyTrainSeatService.countSeat(date, trainCode);
        LOG.info("车次【{}】座位数：{}", trainCode, seatCount);

        long stationCount = dailyTrainStationService.countByTrainCode(trainCode);
        LOG.info("车次【{}】到站数：{}", trainCode, stationCount);

        // 3/4需要根据实际卖票比例来定，一趟火车最多可以卖（seatCount * stationCount）张火车票
        int count = (int) (seatCount * stationCount * 3/4);
        LOG.info("车次【{}】初始生成令牌数：{}", trainCode, count);
        skToken.setCount(count);

        mapper.insert(skToken);
    }


    public void save(SkTokenSaveReq bean) {

        SkToken skToken = BeanUtil.copyProperties(bean, SkToken.class);

        // 当前时间
        DateTime now = DateTime.now();

        if (ObjectUtil.isNull(skToken.getId())) {
            // 新增
            //使用雪花算法生成id
            skToken.setId(SnowUtil.getSnowflakeNextId());
            skToken.setCreateTime(now);
            skToken.setUpdateTime(now);

            mapper.insert(skToken);
        } else {
            skToken.setUpdateTime(now);
            mapper.updateByPrimaryKeySelective(skToken);
        }

    }

    public DBPages<SkTokenQueryRes> queryList(SkTokenQueryReq bean) {

        SkTokenExample example = new SkTokenExample();
        SkTokenExample.Criteria criteria = example.createCriteria();

        // 分页
        PageHelper.startPage(bean.getPageNum(), bean.getPageSize());

        List<SkToken> list = mapper.selectByExample(example);

        List<SkTokenQueryRes> result = BeanUtil.copyToList(list, SkTokenQueryRes.class);

        return DBPages.restPage(result);
    }

    public void delete(Long id) {
        mapper.deleteByPrimaryKey(id);
    }


    /***
     * @author Mr.Liu
     * @date 2023/6/10 18:04
     * @param date  日期
     * @param trainCode  车次
     * @param accountId  账号id
     * @return boolean
     */
    public boolean validSkToken(Date date, String trainCode, Long accountId) {
        LOG.info("会员【{}】获取日期【{}】车次【{}】的令牌开始", accountId, DateUtil.formatDate(date), trainCode);
        // 令牌约等于库存，令牌没有了，就不再卖票，不需要再进入购票主流程去判断库存，判断令牌肯定比判断库存效率高
        int updateCount = skTokenMapper.decrease(date, trainCode);
        return updateCount > 0;
    }
}
