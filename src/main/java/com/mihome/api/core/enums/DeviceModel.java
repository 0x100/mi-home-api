package com.mihome.api.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DeviceModel implements ValueEnum<String> {
    CUBE("cube"),
    MAGNET("magnet"),
    PLUG("plug"),
    MOTION("motion"),
    SWITCH("switch");

    private String value;


    public static DeviceModel of(String value) {
        return (DeviceModel) ValueEnum.find(value, values());
    }
}
