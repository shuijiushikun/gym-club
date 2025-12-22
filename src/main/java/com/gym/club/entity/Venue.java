package com.gym.club.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Venue {
    private Integer id;
    private String name;
    private Integer type;  // 1团课教室，2私教区，3自由训练区，4游泳池，5瑜伽室
    private BigDecimal area;  // 面积
    private Integer capacity;  // 容纳人数
    private String equipmentIds;  // 关联器械ID
    private Integer status;  // 0关闭，1开放，2维护中
    private String description;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}