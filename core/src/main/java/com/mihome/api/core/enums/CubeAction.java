package com.mihome.api.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CubeAction implements ValueEnum<String> {
    FLIP_90("flip90"),
    FLIP_180("flip180"),
    MOVE("move"),
    TAP_TWICE("tap_twice"),
    SHAKE("shake_air"),
    SWING("swing"),
    ALERT("alert"),
    FREE_FALL("free_fall"),
    ROTATE("rotate");

    private String value;


    public static CubeAction of(String value) {
        return (CubeAction) ValueEnum.findOrThrowException(value, values());
    }
}
