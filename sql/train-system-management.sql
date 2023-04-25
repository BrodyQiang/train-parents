# 不带格式粘贴SQL 快捷键是 Ctrl + Shift + V

# 账号信息表 表中字段应该有：手机号
CREATE TABLE `account_info` (
                              `id` bigint(20) NOT NULL COMMENT 'id',
                              `mobile` varchar(11) NOT NULL COMMENT '手机号',
                              `password` varchar(255) DEFAULT NULL COMMENT '密码',
                              `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
                              `update_time` datetime DEFAULT NULL,
                              `create_by` varchar(64) CHARACTER SET utf8 DEFAULT NULL,
                              `update_by` varchar(64) CHARACTER SET utf8 DEFAULT NULL,
                              `delete_flag` char(1) CHARACTER SET utf8 NOT NULL DEFAULT '0' COMMENT '删除标识''0''未删除，''1''已删除',
                              PRIMARY KEY (`id`),
                              UNIQUE KEY `mobile_unique` (`mobile`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='账号信息表';
-- drop table if exists `account_info`;
-- create table `account_info` (
--   `id` bigint not null comment 'id',
--   `mobile` varchar(11) comment '手机号',
--   primary key (`id`),
--   unique key `mobile_unique` (`mobile`)
-- ) engine=innodb default charset=utf8mb4 comment='账号信息表';

# 短息记录表 表中字段应该有：手机号、验证码、发送时间、过期时间、发送状态、发送类型
drop table if exists `sms_record`;
create table `sms_record` (
  `id` bigint not null comment 'id',
  `mobile` varchar(11) comment '手机号',
  `code` varchar(6) comment '验证码',
  `send_time` datetime comment '发送时间',
  `expire_time` datetime comment '过期时间',
  `send_status` varchar(2) comment '发送状态 0-已使用 1-未使用 2-已过期',
  `send_type` varchar(2) comment '发送类型 0-注册 1-登录 2-找回密码 3-修改密码 4-修改手机号 5-绑定手机号 6-解绑手机号 7-其他',
  primary key (`id`),
  unique key `mobile_unique` (`mobile`)
) engine=innodb default charset=utf8mb4 comment='短信记录表';