/*
Navicat MySQL Data Transfer

Source Server         : localhostDB
Source Server Version : 50527
Source Host           : localhost:3306
Source Database       : my_kad_db

Target Server Type    : MYSQL
Target Server Version : 50527
File Encoding         : 65001

Date: 2013-11-21 11:33:40
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `act_id_group`
-- ----------------------------
DROP TABLE IF EXISTS `act_id_group`;
CREATE TABLE `act_id_group` (
  `ID_` varchar(64) COLLATE utf8_bin NOT NULL DEFAULT '',
  `REV_` int(11) DEFAULT NULL,
  `NAME_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `TYPE_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`ID_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of act_id_group
-- ----------------------------
INSERT INTO `act_id_group` VALUES ('erp', '1', 'erp部门', 'assignment');
INSERT INTO `act_id_group` VALUES ('hr', '1', '人事', 'assignment');

-- ----------------------------
-- Table structure for `act_id_membership`
-- ----------------------------
DROP TABLE IF EXISTS `act_id_membership`;
CREATE TABLE `act_id_membership` (
  `USER_ID_` varchar(64) COLLATE utf8_bin NOT NULL DEFAULT '',
  `GROUP_ID_` varchar(64) COLLATE utf8_bin NOT NULL DEFAULT '',
  PRIMARY KEY (`USER_ID_`,`GROUP_ID_`),
  KEY `ACT_FK_MEMB_GROUP` (`GROUP_ID_`),
  CONSTRAINT `ACT_FK_MEMB_GROUP` FOREIGN KEY (`GROUP_ID_`) REFERENCES `act_id_group` (`ID_`),
  CONSTRAINT `ACT_FK_MEMB_USER` FOREIGN KEY (`USER_ID_`) REFERENCES `act_id_user` (`ID_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of act_id_membership
-- ----------------------------
INSERT INTO `act_id_membership` VALUES ('huyang', 'erp');
INSERT INTO `act_id_membership` VALUES ('junhui', 'erp');
INSERT INTO `act_id_membership` VALUES ('shaowei', 'erp');
INSERT INTO `act_id_membership` VALUES ('wangbin', 'erp');
INSERT INTO `act_id_membership` VALUES ('wangqin', 'erp');
INSERT INTO `act_id_membership` VALUES ('hruser', 'hr');
INSERT INTO `act_id_membership` VALUES ('hruser1', 'hr');

-- ----------------------------
-- Table structure for `act_id_user`
-- ----------------------------
DROP TABLE IF EXISTS `act_id_user`;
CREATE TABLE `act_id_user` (
  `ID_` varchar(64) COLLATE utf8_bin NOT NULL DEFAULT '',
  `REV_` int(11) DEFAULT NULL,
  `FIRST_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `LAST_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `EMAIL_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `PWD_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `PICTURE_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`ID_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of act_id_user
-- ----------------------------
INSERT INTO `act_id_user` VALUES ('hruser', '1', 'Lili', 'Zhang', 'hr@gmail.com', '000000', '');
INSERT INTO `act_id_user` VALUES ('hruser1', '1', 'Hr', 'User1', 'huuser1@gmail.com', '000000', null);
INSERT INTO `act_id_user` VALUES ('huyang', '1', 'Yang', 'Hu', 'huyang@gmail.com', '000000', null);
INSERT INTO `act_id_user` VALUES ('junhui', '1', 'Hui', 'Jun', 'junhui@gmail.com', '000000', null);
INSERT INTO `act_id_user` VALUES ('shaowei', '1', 'Jin', 'ShaoWei', 'shaowei@gmail.com', '000000', null);
INSERT INTO `act_id_user` VALUES ('wangbin', '1', 'Bin', 'Wang', 'wangbin@gmail.com', '000000', null);
INSERT INTO `act_id_user` VALUES ('wangqin', '1', 'Qin', 'Wang', 'wangqin@gmail.com', '000000', null);
