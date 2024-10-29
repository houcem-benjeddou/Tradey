package com._INFINI.PI.entities;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Permission {

    ADMIN_READ("admin:read"),
    ADMIN_UPDATE("admin:update"),
    ADMIN_CREATE("admin:create"),
    ADMIN_DELETE("admin:delete"),
    TRADER_READ("trader:read"),
    TRADER_UPDATE("trader:update"),
    TRADER_CREATE("trader:create"),
    TRADER_DELETE("trader:delete"),

    ;

    @Getter
    private final String permission;
}
