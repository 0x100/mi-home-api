package com.mihome.api.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DeviceAction implements ValueEnum<String> {
    ON("on"),
    OFF("off"),
    UNKNOWN("unknown"); // probably device is offline

    private String value;


    public static DeviceAction of(String value) {
        return (DeviceAction) ValueEnum.findOrThrowException(value, values());
    }
}
