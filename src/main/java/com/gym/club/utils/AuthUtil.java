package com.gym.club.utils;

import org.springframework.stereotype.Component;

@Component
public class AuthUtil {

    /**
     * 检查用户是否有权限访问
     */
    public boolean checkPermission(String token, String role, Integer userId, String requiredRole) {
        // 简单验证token格式
        if (token == null || !token.startsWith("simulated-token-")) {
            return false;
        }

        // 检查角色权限
        if (requiredRole == null) {
            return true; // 不需要特定角色
        }

        // ADMIN可以访问所有
        if ("ADMIN".equals(role)) {
            return true;
        }

        // 检查角色匹配
        return requiredRole.equals(role);
    }

    /**
     * 检查是否是会员本人操作
     */
    public boolean isSelfOrAdmin(String token, String role, Integer userId, Integer targetUserId) {
        if (token == null || !token.startsWith("simulated-token-")) {
            return false;
        }

        // ADMIN可以操作所有
        if ("ADMIN".equals(role)) {
            return true;
        }

        // 会员只能操作自己
        return userId.equals(targetUserId);
    }
}