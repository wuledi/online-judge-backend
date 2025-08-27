package com.wuledi.security.enums;

import lombok.Getter;

@Getter
public enum UserRoleEnum {
    NOT_LOGIN(-1, "ROLE_NOT_LOGIN"),
    USER(0, "ROLE_USER"),
    ADMIN(1, "ROLE_ADMIN"),
    BAN(2, "ROLE_BANNED");

    private final int code;
    private final String authority; // 适配Spring Security的GrantedAuthority

    UserRoleEnum(int code, String authority) {
        this.code = code;
        this.authority = authority;
    }

    public static UserRoleEnum getEnumByValue(int code) {
        for (UserRoleEnum role : values()) {
            if (role.code == code) {
                return role;
            }
        }
        return null;
    }
}
