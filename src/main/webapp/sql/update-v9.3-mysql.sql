

alter table jc_acquisition add column def_type_img tinyint(1) not null comment '是否默认类型图0：否1：是';
alter table jc_acquisition add column type_img_start varchar(255) comment '类型图开始';
alter table jc_acquisition add column type_img_end varchar(255) comment '类型图结束';
alter table jc_acquisition add column content_page_prefix varchar(255) comment '内容分页地址补全';
alter table jc_acquisition add column content_page_start varchar(255) comment '内容分页开始';
alter table jc_acquisition add column content_page_end varchar(255) comment '内容分页结束';
alter table jc_acquisition add column page_link_start varchar(255) comment '内容分页链接开始';
alter table jc_acquisition add column page_link_end varchar(255) comment '内容分页链接结束';

CREATE TABLE `jc_acquisition_replace` (
  `replace_id` int(11) NOT NULL auto_increment,
  `acquisition_id` int(11) NOT NULL,
  `keyword` varchar(255) comment '关键词',
  `replace_word` varchar(255) comment '替换词',
  PRIMARY KEY  (`replace_id`),
  KEY `fk_jc_acquisition_replace` (`acquisition_id`),
  CONSTRAINT `fk_jc_acquisition_replace` FOREIGN KEY (`acquisition_id`) REFERENCES `jc_acquisition` (`acquisition_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='采集内容替换';

CREATE TABLE `jc_acquisition_shield` (
  `shield_id` int(11) NOT NULL auto_increment,
  `acquisition_id` int(11) NOT NULL,
  `shield_start` varchar(255) comment '屏蔽开始',
  `shield_end` varchar(255) comment '屏蔽结束',
  PRIMARY KEY  (`shield_id`),
  KEY `fk_jc_acquisition_shield` (`acquisition_id`),
  CONSTRAINT `fk_jc_acquisition_shield` FOREIGN KEY (`acquisition_id`) REFERENCES `jc_acquisition` (`acquisition_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='采集内容屏蔽';
