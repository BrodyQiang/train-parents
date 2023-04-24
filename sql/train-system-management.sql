# 不带格式粘贴SQL 快捷键是 Ctrl + Shift + V

# 账号信息表 表中字段应该有：手机号
drop table if exists `account_info`;
create table `account_info` (
  `id` bigint not null comment 'id',
  `mobile` varchar(11) comment '手机号',
  primary key (`id`),
  unique key `mobile_unique` (`mobile`)
) engine=innodb default charset=utf8mb4 comment='账号信息表';

# 短息记录表 表中字段应该有：手机号、验证码、发送时间 时间为当前时间、过期时间 时间为当前时间、发送状态 (varchar) 0-已使用 1-未使用 2-已过期 默认为0、发送类型(varchar)  0-注册 1-登录 2-找回密码 3-修改密码 4-修改手机号 5-绑定手机号 6-解绑手机号 7-其他 默认为1
drop table if exists `sms_record`;
create table `sms_record` (
  `id` bigint not null comment 'id',
  `mobile` varchar(11) comment '手机号',
  `code` varchar(6) comment '验证码',
  `send_time` datetime comment '发送时间',
  `expire_time` datetime comment '过期时间',
  `send_status` varchar(2)  comment '发送状态 0-已使用 1-未使用 2-已过期 ',
  `send_type` varchar(2) comment '发送类型 0-注册 1-登录 2-找回密码 3-修改密码 4-修改手机号 5-绑定手机号 6-解绑手机号 7-其他',
  primary key (`id`),
  unique key `mobile_unique` (`mobile`)
) engine=innodb default charset=utf8mb4 comment='短信记录表';