package com.gym.club.service;

import com.gym.club.dto.LoginResponse;
import com.gym.club.entity.Member;
import com.gym.club.entity.Coach;
import com.gym.club.mapper.MemberMapper;
import com.gym.club.mapper.CoachMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    @Autowired
    private MemberMapper memberMapper;

    @Autowired
    private CoachMapper coachMapper;

    @Autowired
    private com.gym.club.utils.JwtUtils jwtUtils;

    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @org.springframework.beans.factory.annotation.Value("${gym.admin.username}")
    private String adminUsername;

    @org.springframework.beans.factory.annotation.Value("${gym.admin.password}")
    private String adminPassword;

    /**
     * 简单登录验证
     */
    public LoginResponse login(String username, String password, String role) {
        LoginResponse response = null;

        // 会员登录
        if ("member".equalsIgnoreCase(role)) {
            Member member = memberMapper.selectByUsername(username);
            if (member == null) {
                throw new RuntimeException("会员账号不存在");
            }
            boolean passwordMatch = false;
            if (passwordEncoder.matches(password, member.getPassword())) {
                passwordMatch = true;
            } else if (password.equals(member.getPassword())) {
                // Legacy plain text handling - auto upgrade
                passwordMatch = true;
                member.setPassword(passwordEncoder.encode(password));
                memberMapper.update(member);
            }

            if (!passwordMatch) {
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
            response.setToken(jwtUtils.generateToken(username, "MEMBER", member.getId()));
        }

        // 教练登录
        else if ("coach".equalsIgnoreCase(role)) {
            Coach coach = coachMapper.selectByUsername(username);
            if (coach == null) {
                throw new RuntimeException("教练账号不存在");
            }
            boolean passwordMatch = false;
            if (passwordEncoder.matches(password, coach.getPassword())) {
                passwordMatch = true;
            } else if (password.equals(coach.getPassword())) {
                // Legacy plain text handling - auto upgrade
                passwordMatch = true;
                coach.setPassword(passwordEncoder.encode(password));
                coachMapper.update(coach);
            }

            if (!passwordMatch) {
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
            response.setToken(jwtUtils.generateToken(username, "COACH", coach.getId()));
        }

        // 管理员登录（配置）
        else if ("admin".equalsIgnoreCase(role)) {
            if (!adminUsername.equals(username) || !adminPassword.equals(password)) {
                throw new RuntimeException("管理员账号或密码错误");
            }
            response = new LoginResponse(
                    0,
                    "admin",
                    "ADMIN",
                    "系统管理员");
            response.setToken(jwtUtils.generateToken(adminUsername, "ADMIN", 0));
        } else {
            throw new RuntimeException("未知角色类型");
        }

        return response;
    }
}