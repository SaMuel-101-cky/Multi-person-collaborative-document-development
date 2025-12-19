package com.example.db_document.pojo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum PermissionType {
    OWNER("owner", 3),
    EDITOR("editor", 2),
    VIEWER("viewer", 1);

    private final String value;
    private final int level;

    PermissionType(String value , int level) {
        this.value = value;
        this.level = level;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    // 判断是否有足够的权限 (当前权限 >= 需要的权限)
    public boolean hasPermission(PermissionType required) {
        return this.level >= required.level;
    }
    @JsonCreator
    public static PermissionType fromValue(String v) {
        if (v == null) return null;
        switch (v.toLowerCase()) {
            case "owner": return OWNER;
            case "editor": return EDITOR;
            case "viewer": return VIEWER;
            default: throw new IllegalArgumentException("Unknown permission type: " + v);
        }
    }
}
