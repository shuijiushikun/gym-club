package com.gym.club.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class Coach {
    private Integer id;
    private String username;
    private String password;
    private String realName;
    private String phone;
    private String email;
    private Integer gender; // 0女，1男
    private LocalDate birthday;
    private String avatarUrl;
    private String certificate; // 证书
    private String specialty; // 专长
    private Integer experienceYears; // 从业年限
    private BigDecimal hourlyRate; // 时薪
    private Integer status; // 0离职，1在职
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}