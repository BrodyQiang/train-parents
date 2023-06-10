package com.train.common.controller;


import com.train.common.exception.BusinessException;
import com.train.common.response.DBResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.stream.Collectors;

/**
 * 统一异常处理、数据预处理等
 * 捕获所有Controller抛出的异常
 */
@ControllerAdvice
public class ControllerExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ControllerExceptionHandler.class);

    /**
     * 所有异常统一处理
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public DBResult exceptionHandler(Exception e) throws Exception {
        LOG.error("系统异常：", e);
//        LOG.info("seata全局事务ID: {}", RootContext.getXID());
//        // 如果是在一次全局事务里出异常了，就不要包装返回值，将异常抛给调用方，让调用方回滚事务
//        if (StrUtil.isNotBlank(RootContext.getXID())) {
//            throw e;
//        }
        return DBResult.fail("系统出现异常，请联系管理员");

    }

    /**
     * 业务异常统一处理
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = BusinessException.class)
    @ResponseBody
    public DBResult exceptionHandler(BusinessException e) {
        LOG.error("业务异常：{}", e.getError().getDesc());
        return DBResult.fail(e.getError().getDesc());
    }

    /**
     * 校验异常统一处理
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = BindException.class)
    @ResponseBody
    public DBResult exceptionHandler(BindException e) {
        LOG.error("校验异常：{}", e.getBindingResult().getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList()));
        return DBResult.fail(e.getBindingResult().getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList()).toString());
    }

    /**
     * 校验异常统一处理
     * @param e
     * @return
     */
    @ExceptionHandler(value = RuntimeException.class)
    @ResponseBody
    public DBResult exceptionHandler(RuntimeException e) {
        throw e;
    }


}
