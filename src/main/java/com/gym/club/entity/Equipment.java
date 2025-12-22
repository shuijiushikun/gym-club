package com.gym.club.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class Equipment {
    private Integer id;
    private String name;
    private String type; // 有氧、力量、自由重量
    private String brand;
    private String model;
    private String serialNumber;
    private LocalDate purchaseDate;
    private BigDecimal purchasePrice;
    private Integer usageHours; // 使用时长（小时）
    private Integer maintenanceInterval; // 保养间隔（小时）
    private LocalDate lastMaintenanceDate;
    private LocalDate nextMaintenanceDate;
    private Integer status; // 0报废，1正常，2维修中，3保养中
    private String location;
    private String notes;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}