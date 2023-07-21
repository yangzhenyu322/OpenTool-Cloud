package com.opentool.dashboard.common.enums;

/**
 * / @Author: ZenSheep
 * / @Date: 2023/7/20 2:28
 */
public enum UserStatus {
    NORMAL('0', "正常"), DISABLE('1', "停用"), DELETE('2', "注销");

    private final char code;
    private final String status;

    UserStatus(char code, String status) {
        this.code = code;
        this.status = status;
    }

    public char getCode() { return  code; }

    public String getStatus() { return status; }
}
