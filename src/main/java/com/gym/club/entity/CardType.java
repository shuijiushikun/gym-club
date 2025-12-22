package com.gym.club.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CardType {
    private Integer id;
    private String name;
    private Integer durationDays;
    private BigDecimal price;
    private String description;
    private Integer status; // 0停售，1在售
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}