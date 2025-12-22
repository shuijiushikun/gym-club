package com.gym.club.entity;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class Member {
    private Integer id;
    private String username;
    private String password;
    private String realName;
    private String phone;
    private String email;
    private Integer gender; // 0女，1男
    private LocalDate birthday;
    private String avatarUrl;
    private Integer status; // 0禁用，1正常
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}