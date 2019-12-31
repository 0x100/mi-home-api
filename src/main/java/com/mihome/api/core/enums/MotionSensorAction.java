package com.mihome.api.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MotionSensorAction implements ValueEnum<String> {
    MOTION("motion");

    private String value;


    public static MotionSensorAction of(String value) {
        return (MotionSensorAction) ValueEnum.findOrThrowException(value, values());
    }
}
