package com.train.common.response;

import com.github.pagehelper.PageInfo;
import com.train.common.enums.ResultCodeEnum;
import com.train.common.request.PageBaseReq;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author Mr.Liu
 * @email yxml2580@163.com
 * @createDate 2023/4/23 15:11
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DBPages<T> implements Serializable {

    // 当前页
    private Integer pageNum;

    // 每页的数量
    private Integer pageSize;

    // 总页数
    private Integer pageTotal;

    // 总条数
    private long total;

    // 总条数
    private List<T> result;

    //状态
    private int code = 200;

    //信息
    private String msg;

    //
    public static <T> DBPages<T> restPage(List<T> list) {
        DBPages<T> result = new DBPages<>();
        PageInfo<T> pageInfo = new PageInfo<>(list);
        result.setPageTotal(pageInfo.getPages());
        result.setTotal(pageInfo.getTotal());
        result.setPageNum(pageInfo.getPageNum());
        result.setPageSize(pageInfo.getPageSize());
        result.setResult(pageInfo.getList());
        return result;
    }

    public DBPages(Integer pageNum, Integer pageSize, Integer pageTotal, long total, List<T> result) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.pageTotal = pageTotal;
        this.total = total;
        this.result = result;
    }

    public DBPages(int code, String msg) {
        this.code = code;
        this.msg = msg;
        this.result = null;
    }

    public DBPages(ResultCodeEnum resultCodeEnum, List<T> result) {
        if (resultCodeEnum != null) {
            this.code = resultCodeEnum.getCode();
            this.msg = resultCodeEnum.getMsg();
        }
        this.result = result;
    }

    public static <T> DBPages<T> emptyDBPage(PageBaseReq param) {
        DBPages<T> result = new DBPages<>();
        result.setPageNum(param.getPageNum());
        result.setPageSize(param.getPageSize());
        result.setPageTotal(0);
        result.setTotal(0L);
        result.setResult(null);
        return result;
    }

    public static <T> DBPages<T> fail(String msg) {
        return new DBPages<>(ResultCodeEnum.LOGIC.getCode(), msg);
    }

    public static <T> DBPages<T> fail() {
        return DBPages.fail("");
    }

    public static <T> DBPages<T> success(List<T> result) {
        return DBPages.restPage(result);
    }

}
