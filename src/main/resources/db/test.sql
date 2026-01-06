
-- ----------------------------
-- Table structure for test
-- ----------------------------
DROP TABLE IF EXISTS `test`;
CREATE TABLE `test` (
  `id` varchar(255) NOT NULL COMMENT '序列',
  `name` varchar(255) NOT NULL COMMENT '名称',
  `picture` varchar(255) DEFAULT NULL COMMENT '图片地址',
  `url` varchar(255) DEFAULT NULL COMMENT '跳转地址',
  `enable` bit(1) DEFAULT b'0' COMMENT '是否启用',
  `description` varchar(255) DEFAULT NULL COMMENT '说明',
  `create_date_time` datetime DEFAULT NULL COMMENT '创建时间',
  `create_name` varchar(255) DEFAULT NULL COMMENT '创建人',
  `modify_date_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `modify_name` varchar(255) DEFAULT NULL COMMENT '修改人',
  `is_delete` bit(1) DEFAULT b'0' COMMENT '删除状态',
  `type` int(20) DEFAULT '0' COMMENT '类型（0：首页，1：百万年薪，2: 报价方案， 3：工保百科，4：测试，5：测试2）',
  `state` int(20) DEFAULT '0' COMMENT '状态（0：测试）',
  `label` varchar(255) DEFAULT NULL COMMENT '标签',
  `sorting` int(20) DEFAULT '0' COMMENT '排序',
  `version` int(20) DEFAULT '1' COMMENT '版本号',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='测试生成';

