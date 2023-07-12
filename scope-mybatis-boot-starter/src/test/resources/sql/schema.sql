DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`
(
    `user_id`     bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `dept_id`     bigint(20)   NULL DEFAULT NULL COMMENT '部门ID',
    `user_name`   varchar(30)  NOT NULL COMMENT '用户账号',
    `nick_name`   varchar(30)  NOT NULL COMMENT '用户昵称',
    `user_type`   varchar(2)   NULL DEFAULT '00' COMMENT '用户类型（00系统用户）',
    `email`       varchar(50)  NULL DEFAULT '' COMMENT '用户邮箱',
    `phonenumber` varchar(11)  NULL DEFAULT '' COMMENT '手机号码',
    `sex`         char(1)      NULL DEFAULT '0' COMMENT '用户性别（0男 1女 2未知）',
    `avatar`      varchar(100) NULL DEFAULT '' COMMENT '头像地址',
    `password`    varchar(100) NULL DEFAULT '' COMMENT '密码',
    `status`      char(1)      NULL DEFAULT '0' COMMENT '帐号状态（0正常 1停用）',
    `del_flag`    char(1)      NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
    `login_ip`    varchar(128) NULL DEFAULT '' COMMENT '最后登录IP',
    `login_date`  datetime     NULL DEFAULT NULL COMMENT '最后登录时间',
    `create_by`   varchar(64)  NULL DEFAULT '' COMMENT '创建者',
    `create_time` datetime     NULL DEFAULT NULL COMMENT '创建时间',
    `update_by`   varchar(64)  NULL DEFAULT '' COMMENT '更新者',
    `update_time` datetime     NULL DEFAULT NULL COMMENT '更新时间',
    `remark`      varchar(500) NULL DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`user_id`)
);

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user`
VALUES (1, 103, 'admin', '平台', '00', 'ry@163.com', '15888888888', '1',
        '/profile/avatar/2022/12/27/blob_20221227014707A001.jpeg',
        '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', '2023-01-06 14:01:27',
        'admin', '2022-12-02 07:55:12', '', '2023-01-06 06:00:06', '管理员');
INSERT INTO `sys_user`
VALUES (2, 105, 'ry', '平台', '00', 'ry@qq.com', '15666666666', '1', '',
        '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', '2022-12-02 07:55:12',
        'admin', '2022-12-02 07:55:12', '', NULL, '测试员');
INSERT INTO `sys_user`
VALUES (100, 103, '李**', 'liweihong123', '00', '', '', '0', '',
        '$2a$10$Vq9W1Z.G4sxpW.WTGZvSKuwx55AQAJeC4RALWXCahnTV2uugb4z1S', '0', '2', '', NULL, 'admin',
        '2022-12-02 09:14:14', 'admin', '2022-12-02 09:15:53', NULL);
INSERT INTO `sys_user`
VALUES (101, NULL, 'liweihong123', '李**', '00', '', '', '0', '',
        '$2a$10$kyI1zzp/yREKtdrH69pOhOOy.XpZHU8c/Jmv38sue4Na6Kp9B.qAe', '0', '0', '127.0.0.1', '2022-12-28 16:26:18',
        'admin', '2022-12-02 09:17:40', 'admin', '2022-12-28 08:26:12', NULL);
INSERT INTO `sys_user`
VALUES (102, 103, 'zhb123', '张**', '00', '', '', '0', '',
        '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2',
        '0', '0', '127.0.0.1', '2022-12-30 15:50:51', 'admin', '2022-12-02 09:22:48', '', '2022-12-30 07:49:33', NULL);
INSERT INTO `sys_user`
VALUES (103, NULL, 'xty123', '席**', '00', '', '', '0', '',
        '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2',
        '0', '0', '127.0.0.1', '2023-01-06 10:02:10', 'admin', '2022-12-02 09:23:11', '', '2023-01-06 02:02:04', NULL);
