package com.mihome.api.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DoorWindowSensorAction implements ValueEnum<String> {
    OPEN("open"),
    CLOSE("close");

    private String value;


    public static DoorWindowSensorAction of(String value) {
        return (DoorWindowSensorAction) ValueEnum.findOrThrowException(value, values());
    }
}
