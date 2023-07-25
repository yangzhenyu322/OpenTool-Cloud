package com.opentool.common.core.enums;

/**
 * / @Author: ZenSheep
 * / @Date: 2023/7/20 2:58
 */
public enum UserRole {
    ADMIN(1L, "超级管理员"), VIP(2L, "VIP用户"), COMMON(3L, "普通用户");

    private final Long code;
    private final String role;

    UserRole(Long code, String role) {
        this.code = code;
        this.role = role;
    }

    public Long getCode() { return code; }
    public String getRole() {return role; }
}
