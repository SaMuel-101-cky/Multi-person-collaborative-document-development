package com.example.db_document.pojo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum PermissionType {
    OWNER("owner"),
    EDITOR("editor"),
    VIEWER("viewer");

    private final String value;

    PermissionType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
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
