package com.train.controller.web;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.train.bean.request.ConfirmOrderSaveReq;
import com.train.common.enums.BusinessExceptionEnum;
import com.train.common.exception.BusinessException;
import com.train.common.response.DBResult;
import com.train.service.ConfirmOrderService;
import com.train.service.DailyTrainCarriageService;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
* @author Mr.Liu
* @email yxml2580@163.com
* @createDate 2023-05-16 18:54:37
*/

@RestController
@RequestMapping("/confirmOrder")
@Api(value = "ConfirmOrderController", tags = "")
public class ConfirmOrderWebController {

    private static final Logger LOG = LoggerFactory.getLogger(ConfirmOrderWebController.class);

    @Autowired
    private ConfirmOrderService service;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @PostMapping("/doConfirm")
    @SentinelResource(value = "confirmOrderDoConfirm", blockHandler = "doConfirmBlock")
    public DBResult doConfirm(@RequestBody @Valid ConfirmOrderSaveReq bean) {

        // 图形验证码校验
        String imageCodeToken = bean.getImageCodeToken();
        String imageCode = bean.getImageCode();
        String imageCodeRedis = redisTemplate.opsForValue().get(imageCodeToken);
        LOG.info("从redis中获取到的验证码：{}", imageCodeRedis);
        if (ObjectUtils.isEmpty(imageCodeRedis)) {
            return DBResult.fail("验证码已过期");
        }
        // 验证码校验，大小写忽略，提升体验，比如Oo Vv Ww容易混
        if (!imageCodeRedis.equalsIgnoreCase(imageCode)) {
            return DBResult.fail("验证码不正确");
        } else {
            // 验证通过后，移除验证码
            redisTemplate.delete(imageCodeToken);
        }

        service.doConfirm(bean);
        return DBResult.success();
    }

    /**
     * 降级方法，需包含限流方法的所有参数和BlockException参数
     * @param bean
     * @param e
     */
    public DBResult doConfirmBlock(ConfirmOrderSaveReq bean, BlockException e) {
        LOG.info("购票请求被限流：{}", bean);
        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_FLOW_EXCEPTION);
    }
}
