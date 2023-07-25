package com.opentool.common.core.enums;

/**
 * / @Author: ZenSheep
 * / @Date: 2023/7/20 2:16
 */
public enum UserSex {
    MALE('0', "男"), WOMAN('1', "女");

    private final char code;
    private final String sex;

    UserSex(char code, String sex) {
        this.code =code;
        this.sex = sex;
    }

    public char getCode() { return code; }

    public String getSex() { return  sex; }
}
