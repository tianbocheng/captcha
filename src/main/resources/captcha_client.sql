/*
 Navicat Premium Data Transfer

 Source Server         : 192.168.150.130
 Source Server Type    : MySQL
 Source Server Version : 50716
 Source Host           : 192.168.150.130:3306
 Source Schema         : pig

 Target Server Type    : MySQL
 Target Server Version : 50716
 File Encoding         : 65001

 Date: 07/08/2018 11:21:06
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for captcha_client
-- ----------------------------
DROP TABLE IF EXISTS `captcha_client`;
CREATE TABLE `captcha_client`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `client_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_german2_ci NULL DEFAULT NULL COMMENT '客户端ID',
  `client_security` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_german2_ci NULL DEFAULT NULL COMMENT '客户端签名',
  `create_date` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_date` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `UK7gj95ew5q62071292g5a4ou62`(`client_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_german2_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of captcha_client
-- ----------------------------
INSERT INTO `captcha_client` VALUES (1, 'teleinfo', 'teleinfo', '2018-07-27 18:03:30', NULL);

SET FOREIGN_KEY_CHECKS = 1;
