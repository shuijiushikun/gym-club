package com.gym.club.controller;

import com.gym.club.dto.LoginRequest;
import com.gym.club.dto.LoginResponse;
import com.gym.club.entity.Member;
import com.gym.club.entity.Coach;
import com.gym.club.mapper.MemberMapper;
import com.gym.club.mapper.CoachMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
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
            String username = loginRequest.getUsername();
            String password = loginRequest.getPassword();
            String role = loginRequest.getRole();

            LoginResponse response = null;

            // 1. 会员登录
            if ("member".equalsIgnoreCase(role)) {
                Member member = memberMapper.selectByUsername(username);
                if (member == null) {
                    throw new RuntimeException("会员账号不存在");
                }
                if (!password.equals(member.getPassword())) {
                    throw new RuntimeException("密码错误");
                }
                if (member.getStatus() != 1) {
                    throw new RuntimeException("账号已被禁用");
                }
                response = new LoginResponse(
                        member.getId(),
                        member.getUsername(),
                        "MEMBER",
                        member.getRealName());
            }

            // 2. 教练登录
            else if ("coach".equalsIgnoreCase(role)) {
                Coach coach = coachMapper.selectByUsername(username);
                if (coach == null) {
                    throw new RuntimeException("教练账号不存在");
                }
                if (!password.equals(coach.getPassword())) {
                    throw new RuntimeException("密码错误");
                }
                if (coach.getStatus() != 1) {
                    throw new RuntimeException("账号已被禁用");
                }
                response = new LoginResponse(
                        coach.getId(),
                        coach.getUsername(),
                        "COACH",
                        coach.getRealName());
            }

            // 3. 管理员登录（硬编码）
            else if ("admin".equalsIgnoreCase(role)) {
                if (!"admin".equals(username) || !"admin123".equals(password)) {
                    throw new RuntimeException("管理员账号或密码错误");
                }
                response = new LoginResponse(
                        0,
                        "admin",
                        "ADMIN",
                        "系统管理员");
            }

            // 4. 角色错误
            else {
                throw new RuntimeException("未知角色类型，请选择：member、coach、admin");
            }

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