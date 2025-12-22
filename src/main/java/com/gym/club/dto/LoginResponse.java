package com.gym.club.dto;

import lombok.Data;

@Data
public class LoginResponse {
    private Integer userId;
    private String username;
    private String role; // "MEMBER", "COACH", "ADMIN"
    private String realName;

    public LoginResponse(Integer userId, String username, String role, String realName) {
        this.userId = userId;
        this.username = username;
        this.role = role;
        this.realName = realName;
    }
}