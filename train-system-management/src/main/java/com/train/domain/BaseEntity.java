package com.train.domain;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.Version;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * Created by JoinIn
 * DATE: 4/19/22
 **/
@Data
@Accessors(chain = true)
public class BaseEntity extends Model {
    private static final long serialVersionUID = -5136375890112140623L;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime createTime;//创建日期

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime updateTime;//更新日期

    private String createBy;//创建人

    private String updateBy;//创建人

    @JSONField(serialize = false)
    @TableLogic
    private String deleteFlag;//删除标记;1:删除;0:未删除
}
