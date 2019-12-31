package com.mihome.api.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Command implements ValueEnum<String> {
    REPORT("report"),
    HEARTBEAT("heartbeat");

    private String value;


    public static Command of(String value) {
        return (Command) ValueEnum.find(value, values());
    }
}
