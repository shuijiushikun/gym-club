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

    /**
     * 简单登录验证
     */
    public LoginResponse login(String username, String password, String role) {
        // 会员登录
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
            return new LoginResponse(
                    member.getId(),
                    member.getUsername(),
                    "MEMBER",
                    member.getRealName());
        }

        // 教练登录
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
            return new LoginResponse(
                    coach.getId(),
                    coach.getUsername(),
                    "COACH",
                    coach.getRealName());
        }

        // 管理员登录（硬编码）
        else if ("admin".equalsIgnoreCase(role)) {
            if (!"admin".equals(username) || !"admin123".equals(password)) {
                throw new RuntimeException("管理员账号或密码错误");
            }
            return new LoginResponse(
                    0,
                    "admin",
                    "ADMIN",
                    "系统管理员");
        }

        throw new RuntimeException("未知角色类型");
    }

    /**
     * 验证token（简化版）
     */
    public boolean validateToken(String token, String role, Integer userId) {
        // 简单验证，实际应该更复杂
        if (token == null || !token.startsWith("simulated-token-")) {
            return false;
        }

        // 检查用户是否存在（根据角色）
        if ("MEMBER".equals(role)) {
            Member member = memberMapper.selectById(userId);
            return member != null && member.getStatus() == 1;
        } else if ("COACH".equals(role)) {
            Coach coach = coachMapper.selectById(userId);
            return coach != null && coach.getStatus() == 1;
        } else if ("ADMIN".equals(role)) {
            return userId == 0;
        }

        return false;
    }
}