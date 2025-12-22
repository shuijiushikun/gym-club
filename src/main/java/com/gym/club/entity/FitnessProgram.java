package com.gym.club.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class FitnessProgram {
    private Integer id;
    private String name;
    private Integer type; // 1团课，2私教课，3自由训练
    private Integer coachId; // 教练ID
    private String description;
    private Integer durationMinutes; // 时长（分钟）
    private Integer maxParticipants; // 最大参与人数
    private BigDecimal price; // 价格
    private Integer difficultyLevel; // 1初级，2中级，3高级
    private Integer status; // 0下架，1上架
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // 关联信息
    private Coach coach;
}