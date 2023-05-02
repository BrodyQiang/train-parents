package com.train.controller;

import com.train.bean.request.${Domain}QueryReq;
import com.train.bean.request.${Domain}SaveReq;
import com.train.bean.response.${Domain}QueryRes;
import com.train.common.response.DBPages;
import com.train.common.response.DBResult;
import com.train.service.${Domain}Service;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
* @author Mr.Liu
* @email yxml2580@163.com
* @createDate ${now?string("yyyy-MM-dd HH:mm:ss")}
*/

@RestController
@RequestMapping("/${domain}")
@Api(value = "${Domain}Controller", tags = "")
public class ${Domain}Controller {

    @Autowired
    private ${Domain}Service service;

    @PostMapping("/save")
    public DBResult save(@RequestBody @Valid ${Domain}SaveReq bean) {
        service.save(bean);
        return DBResult.success();
    }

    @GetMapping("/queryList")
    public DBPages<${Domain}QueryRes> queryList(@Valid ${Domain}QueryReq bean) {
        return service.queryList(bean);
    }

    @DeleteMapping("/delete/{id}")
    public DBResult delete(@PathVariable Long id) {
        service.delete(id);
        return DBResult.success();
    }

}
