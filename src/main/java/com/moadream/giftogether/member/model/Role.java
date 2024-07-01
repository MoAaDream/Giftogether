package com.moadream.giftogether.member.model;

import lombok.Getter;

@Getter
public enum Role {
    ADMIN("ROLE_ADMIN, ROLE_MEMBER"),
    MEMBER("ROLE_MEMBER");

    private final String roleName;

    Role(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }
}
