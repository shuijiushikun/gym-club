package com.gym.club.controller;

import com.gym.club.dto.LoginRequest;
import com.gym.club.dto.LoginResponse;
import com.gym.club.entity.Member;
import com.gym.club.entity.Coach;
import com.gym.club.mapper.MemberMapper;
import com.gym.club.service.AuthService;
import com.gym.club.mapper.CoachMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @Autowired
    private MemberMapper memberMapper;

    @Autowired
    private CoachMapper coachMapper;

    /**
     * 登录接口 - 三个角色通用
     */
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody LoginRequest loginRequest) {
        Map<String, Object> result = new HashMap<>();

        try {
            LoginResponse response = authService.login(loginRequest.getUsername(), loginRequest.getPassword(),
                    loginRequest.getRole());

            result.put("code", 200);
            result.put("message", "登录成功");
            result.put("data", response);

        } catch (RuntimeException e) {
            result.put("code", 400);
            result.put("message", e.getMessage());
            result.put("data", null);
        }

        return result;
    }

    /**
     * 检查用户名是否可用（注册时用）
     */
    @GetMapping("/check-username")
    public Map<String, Object> checkUsername(@RequestParam String username) {
        Map<String, Object> result = new HashMap<>();

        // 检查会员表
        Member member = memberMapper.selectByUsername(username);
        // 检查教练表
        Coach coach = coachMapper.selectByUsername(username);
        // 检查管理员账号
        boolean isAdmin = "admin".equals(username);

        if (member != null || coach != null || isAdmin) {
            result.put("code", 400);
            result.put("message", "用户名已存在");
            result.put("available", false);
        } else {
            result.put("code", 200);
            result.put("message", "用户名可用");
            result.put("available", true);
        }

        return result;
    }
}