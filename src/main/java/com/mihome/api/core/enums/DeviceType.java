package com.mihome.api.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DeviceType implements ValueEnum<String> {
    XIAOMI_GATEWAY_LIGHT("light"),
    XIAOMI_GATEWAY_ILLUMINATION_SENSOR("illumination");

    private String value;
}
