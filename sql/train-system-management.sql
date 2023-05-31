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
  `send_time` datetime default CURRENT_TIMESTAMP not null comment '发送时间',
  `expire_time` datetime default CURRENT_TIMESTAMP not null comment '过期时间',
  `send_status` varchar(2) default 1 not null  comment '发送状态 0-已使用 1-未使用 2-已过期 ',
  `send_type` varchar(2) default 1 not null comment '发送类型 0-注册 1-登录 2-找回密码 3-修改密码 4-修改手机号 5-绑定手机号 6-解绑手机号 7-其他',
  primary key (`id`),
  unique key `mobile_unique` (`mobile`)
) engine=innodb default charset=utf8mb4 comment='短信记录表';


# 账号下的乘车人信息 表中字段应该有：账号id、乘车人姓名、乘车人身份证、乘车人电话、旅客类型
drop table if exists `account_passenger`;
create table `account_passenger` (
  `id` bigint not null comment 'id',
  `member_id` bigint not null comment '账号id',
  `name` varchar(20) not null comment '乘车人姓名',
  `id_card` varchar(18) not null comment '乘车人身份证',
  `phone` varchar(18) not null comment '乘车人电话',
  `type` char(2) not null comment '乘客类型|枚举[PassengerTypeEnum]',
  `create_time` datetime default CURRENT_TIMESTAMP not null comment '新增时间',
  `update_time` datetime default CURRENT_TIMESTAMP not null comment '修改时间',
  primary key (`id`),
  index `member_id_index` (`member_id`)
) engine=innodb default charset=utf8mb4 comment='账号下的乘车人信息';

drop table if exists `ticket`;
create table `ticket` (
  `id` bigint not null comment 'id',
  `member_id` bigint not null comment '会员id',
  `passenger_id` bigint not null comment '乘客id',
  `passenger_name` varchar(20) comment '乘客姓名',
  `train_date` date not null comment '日期',
  `train_code` varchar(20) not null comment '车次编号',
  `carriage_index` int not null comment '箱序',
  `seat_row` char(2) not null comment '排号|01, 02',
  `seat_col` char(1) not null comment '列号|枚举[SeatColEnum]',
  `start_station` varchar(20) not null comment '出发站',
  `start_time` time not null comment '出发时间',
  `end_station` varchar(20) not null comment '到达站',
  `end_time` time not null comment '到站时间',
  `seat_type` char(2) not null comment '座位类型|枚举[SeatTypeEnum]',
  `create_time` datetime(3) comment '新增时间',
  `update_time` datetime(3) comment '修改时间',
  primary key (`id`),
  index `member_id_index` (`member_id`)
) engine=innodb default charset=utf8mb4 comment='车票';

CREATE TABLE `undo_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `branch_id` bigint(20) NOT NULL,
  `xid` varchar(100) NOT NULL,
  `context` varchar(128) NOT NULL,
  `rollback_info` longblob NOT NULL,
  `log_status` int(11) NOT NULL,
  `log_created` datetime NOT NULL,
  `log_modified` datetime NOT NULL,
  `ext` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ux_undo_log` (`xid`,`branch_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
