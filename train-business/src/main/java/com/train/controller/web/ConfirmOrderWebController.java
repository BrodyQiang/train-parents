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

    @PostMapping("/doConfirm")
    @SentinelResource(value = "confirmOrderDoConfirm", blockHandler = "doConfirmBlock")
    public DBResult doConfirm(@RequestBody @Valid ConfirmOrderSaveReq bean) {
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
