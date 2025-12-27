SET NAMES utf8mb4;

CREATE DATABASE IF NOT EXISTS gym_club
    DEFAULT CHARACTER SET = utf8mb4
    DEFAULT COLLATE = utf8mb4_unicode_ci;

USE gym_club;

CREATE TABLE `member`
(
    `id`          INT          NOT NULL AUTO_INCREMENT COMMENT '会员ID',
    `username`    VARCHAR(50)  NOT NULL COMMENT '用户名',
    `password`    VARCHAR(255) NOT NULL COMMENT '密码',
    `real_name`   VARCHAR(50) COMMENT '真实姓名',
    `phone`       VARCHAR(20) COMMENT '手机号',
    `email`       VARCHAR(100) COMMENT '邮箱',
    `gender`      TINYINT COMMENT '性别：0女，1男',
    `birthday`    DATE COMMENT '生日',
    `avatar_url`  VARCHAR(255) COMMENT '头像地址',
    `status`      TINYINT  DEFAULT 1 COMMENT '状态：0禁用，1正常',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='会员表';


CREATE TABLE `coach`
(
    `id`               INT          NOT NULL AUTO_INCREMENT COMMENT '教练ID',
    `username`         VARCHAR(50)  NOT NULL COMMENT '用户名',
    `password`         VARCHAR(255) NOT NULL COMMENT '密码',
    `real_name`        VARCHAR(50) COMMENT '真实姓名',
    `phone`            VARCHAR(20) COMMENT '手机号',
    `email`            VARCHAR(100) COMMENT '邮箱',
    `gender`           TINYINT COMMENT '性别：0女，1男',
    `birthday`         DATE COMMENT '生日',
    `avatar_url`       VARCHAR(255) COMMENT '头像地址',
    `certificate`      VARCHAR(255) COMMENT '教练证书',
    `specialty`        VARCHAR(100) COMMENT '专长（如：瑜伽、力量训练）',
    `experience_years` INT COMMENT '从业年限',
    `hourly_rate`      DECIMAL(10, 2) COMMENT '时薪',
    `status`           TINYINT  DEFAULT 1 COMMENT '状态：0离职，1在职',
    `create_time`      DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='教练表';

CREATE TABLE `card_type`
(
    `id`            INT            NOT NULL AUTO_INCREMENT COMMENT '卡类型ID',
    `name`          VARCHAR(50)    NOT NULL COMMENT '卡类型名称（如：月卡、季卡、年卡）',
    `duration_days` INT            NOT NULL COMMENT '有效天数',
    `price`         DECIMAL(10, 2) NOT NULL COMMENT '价格',
    `description`   VARCHAR(255) COMMENT '描述',
    `status`        TINYINT  DEFAULT 1 COMMENT '状态：0停售，1在售',
    `create_time`   DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_name` (`name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='会员卡类型表';


CREATE TABLE `member_card`
(
    `id`             INT            NOT NULL AUTO_INCREMENT COMMENT '会员卡ID',
    `member_id`      INT            NOT NULL COMMENT '会员ID',
    `card_type_id`   INT            NOT NULL COMMENT '卡类型ID',
    `card_number`    VARCHAR(50)    NOT NULL COMMENT '卡号（可生成唯一编号）',
    `start_date`     DATE           NOT NULL COMMENT '开始日期',
    `end_date`       DATE           NOT NULL COMMENT '结束日期',
    `remaining_days` INT            NOT NULL COMMENT '剩余天数',
    `total_amount`   DECIMAL(10, 2) NOT NULL COMMENT '总金额',
    `paid_amount`    DECIMAL(10, 2) NOT NULL COMMENT '已支付金额',
    `payment_status` TINYINT  DEFAULT 0 COMMENT '支付状态：0未支付，1已支付，2已退款',
    `card_status`    TINYINT  DEFAULT 1 COMMENT '卡状态：0无效，1有效，2已过期，3已挂失',
    `create_time`    DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`    DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_card_number` (`card_number`),
    KEY `idx_member_id` (`member_id`),
    KEY `idx_card_type_id` (`card_type_id`),
    CONSTRAINT `fk_member_card_member` FOREIGN KEY (`member_id`) REFERENCES `member` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_member_card_type` FOREIGN KEY (`card_type_id`) REFERENCES `card_type` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='会员卡表';

CREATE TABLE `payment_record`
(
    `id`             INT            NOT NULL AUTO_INCREMENT COMMENT '缴费记录ID',
    `member_id`      INT            NOT NULL COMMENT '会员ID',
    `order_number`   VARCHAR(50)    NOT NULL COMMENT '订单号',
    `payment_type`   TINYINT        NOT NULL COMMENT '支付类型：1会员卡，2课程，3私教',
    `related_id`     INT COMMENT '关联ID（如会员卡ID、课程ID等）',
    `amount`         DECIMAL(10, 2) NOT NULL COMMENT '支付金额',
    `payment_method` TINYINT        NOT NULL COMMENT '支付方式：1微信，2支付宝，3现金，4银行卡',
    `payment_status` TINYINT  DEFAULT 0 COMMENT '支付状态：0待支付，1支付成功，2支付失败，3已退款',
    `transaction_id` VARCHAR(100) COMMENT '第三方交易ID',
    `pay_time`       DATETIME COMMENT '支付时间',
    `remark`         VARCHAR(255) COMMENT '备注',
    `create_time`    DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`    DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_number` (`order_number`),
    KEY `idx_member_id` (`member_id`),
    KEY `idx_related_id` (`related_id`),
    CONSTRAINT `fk_payment_member` FOREIGN KEY (`member_id`) REFERENCES `member` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='缴费记录表';


CREATE TABLE `fitness_program`
(
    `id`               INT          NOT NULL AUTO_INCREMENT COMMENT '项目ID',
    `name`             VARCHAR(100) NOT NULL COMMENT '项目名称',
    `type`             TINYINT      NOT NULL COMMENT '项目类型：1团课，2私教课，3自由训练',
    `coach_id`         INT COMMENT '教练ID（私教课需要）',
    `description`      TEXT COMMENT '项目描述',
    `duration_minutes` INT          NOT NULL COMMENT '时长（分钟）',
    `max_participants` INT COMMENT '最大参与人数（团课需要）',
    `price`            DECIMAL(10, 2) COMMENT '价格（私教课需要）',
    `difficulty_level` TINYINT COMMENT '难度等级：1初级，2中级，3高级',
    `status`           TINYINT  DEFAULT 1 COMMENT '状态：0下架，1上架',
    `create_time`      DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_coach_id` (`coach_id`),
    CONSTRAINT `fk_program_coach` FOREIGN KEY (`coach_id`) REFERENCES `coach` (`id`) ON DELETE SET NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='健身项目表';

CREATE TABLE `equipment`
(
    `id`                    INT          NOT NULL AUTO_INCREMENT COMMENT '器械ID',
    `name`                  VARCHAR(100) NOT NULL COMMENT '器械名称',
    `type`                  VARCHAR(50) COMMENT '器械类型（如：有氧、力量、自由重量）',
    `brand`                 VARCHAR(50) COMMENT '品牌',
    `model`                 VARCHAR(50) COMMENT '型号',
    `serial_number`         VARCHAR(100) COMMENT '序列号',
    `purchase_date`         DATE COMMENT '购买日期',
    `purchase_price`        DECIMAL(10, 2) COMMENT '购买价格',
    `usage_hours`           INT      DEFAULT 0 COMMENT '使用时长（小时）',
    `maintenance_interval`  INT COMMENT '保养间隔（小时）',
    `last_maintenance_date` DATE COMMENT '上次保养日期',
    `next_maintenance_date` DATE COMMENT '下次保养日期',
    `status`                TINYINT  DEFAULT 1 COMMENT '状态：0报废，1正常，2维修中，3保养中',
    `location`              VARCHAR(100) COMMENT '存放位置',
    `notes`                 TEXT COMMENT '备注',
    `create_time`           DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`           DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_serial_number` (`serial_number`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='健身器械表';

CREATE TABLE `venue`
(
    `id`            INT          NOT NULL AUTO_INCREMENT COMMENT '场地ID',
    `name`          VARCHAR(100) NOT NULL COMMENT '场地名称',
    `type`          TINYINT      NOT NULL COMMENT '场地类型：1团课教室，2私教区，3自由训练区，4游泳池，5瑜伽室',
    `area`          DECIMAL(10, 2) COMMENT '面积（平方米）',
    `capacity`      INT COMMENT '容纳人数',
    `equipment_ids` VARCHAR(255) COMMENT '关联器械ID（逗号分隔）',
    `status`        TINYINT  DEFAULT 1 COMMENT '状态：0关闭，1开放，2维护中',
    `description`   TEXT COMMENT '描述',
    `create_time`   DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_name` (`name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='健身场地表';

CREATE TABLE `booking`
(
    `id`                 INT         NOT NULL AUTO_INCREMENT COMMENT '预约ID',
    `booking_number`     VARCHAR(50) NOT NULL COMMENT '预约单号',
    `member_id`          INT         NOT NULL COMMENT '会员ID',
    `booking_type`       TINYINT     NOT NULL COMMENT '预约类型：1团课，2私教课，3自由训练，4器械预约，5场地预约',
    `related_id`         INT         NOT NULL COMMENT '关联ID（项目ID、教练ID、场地ID等）',
    `coach_id`           INT COMMENT '教练ID（私教课需要）',
    `venue_id`           INT COMMENT '场地ID',
    `start_time`         DATETIME    NOT NULL COMMENT '开始时间',
    `end_time`           DATETIME    NOT NULL COMMENT '结束时间',
    `duration_minutes`   INT         NOT NULL COMMENT '时长（分钟）',
    `participants_count` INT      DEFAULT 1 COMMENT '参与人数',
    `booking_status`     TINYINT  DEFAULT 0 COMMENT '预约状态：0待确认，1已确认，2已取消，3已完成，4已过期',
    `attendance_status`  TINYINT  DEFAULT 0 COMMENT '出勤状态：0未签到，1已签到，2迟到，3缺席',
    `notes`              TEXT COMMENT '备注',
    `cancel_reason`      VARCHAR(255) COMMENT '取消原因',
    `create_time`        DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`        DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_booking_number` (`booking_number`),
    KEY `idx_member_id` (`member_id`),
    KEY `idx_coach_id` (`coach_id`),
    KEY `idx_venue_id` (`venue_id`),
    KEY `idx_start_time` (`start_time`),
    KEY `idx_booking_status` (`booking_status`),
    CONSTRAINT `fk_booking_member` FOREIGN KEY (`member_id`) REFERENCES `member` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_booking_coach` FOREIGN KEY (`coach_id`) REFERENCES `coach` (`id`) ON DELETE SET NULL,
    CONSTRAINT `fk_booking_venue` FOREIGN KEY (`venue_id`) REFERENCES `venue` (`id`) ON DELETE SET NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='预约表';

CREATE TABLE `attendance`
(
    `id`               INT      NOT NULL AUTO_INCREMENT COMMENT '打卡记录ID',
    `member_id`        INT      NOT NULL COMMENT '会员ID',
    `booking_id`       INT COMMENT '关联预约ID',
    `check_in_time`    DATETIME NOT NULL COMMENT '签到时间',
    `check_out_time`   DATETIME COMMENT '签出时间',
    `duration_minutes` INT COMMENT '锻炼时长（分钟）',
    `calories_burned`  INT COMMENT '消耗卡路里',
    `attendance_type`  TINYINT  DEFAULT 1 COMMENT '打卡类型：1正常打卡，2预约打卡',
    `notes`            VARCHAR(255) COMMENT '备注',
    `create_time`      DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_member_id` (`member_id`),
    KEY `idx_check_in_time` (`check_in_time`),
    KEY `idx_booking_id` (`booking_id`),
    CONSTRAINT `fk_attendance_member` FOREIGN KEY (`member_id`) REFERENCES `member` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_attendance_booking` FOREIGN KEY (`booking_id`) REFERENCES `booking` (`id`) ON DELETE SET NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='健身打卡记录表';

INSERT INTO `card_type` (`name`, `duration_days`, `price`, `description`, `status`)
VALUES ('月卡', 30, 299.00, '一个月有效，无限次使用', 1),
       ('季卡', 90, 799.00, '三个月有效，性价比更高', 1),
       ('年卡', 365, 2599.00, '全年有效，最优惠选择', 1);

