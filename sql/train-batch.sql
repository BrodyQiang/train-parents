# 账号信息表 表中字段应该有：手机号
drop table if exists `account_info`;
create table `account_info` (
  `id` bigint not null comment 'id',
  `mobile` varchar(11) comment '手机号',
  primary key (`id`),
  unique key `mobile_unique` (`mobile`)
) engine=innodb default charset=utf8mb4 comment='账号信息表';
