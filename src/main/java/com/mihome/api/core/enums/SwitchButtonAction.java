package com.mihome.api.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SwitchButtonAction implements ValueEnum<String> {
    CLICK("click"),
    DOUBLE_CLICK("double_click"),
    LONG_CLICK_PRESS("long_click_press"),
    LONG_CLICK_RELEASE("long_click_release");

    private String value;


    public static SwitchButtonAction of(String value) {
        return (SwitchButtonAction) ValueEnum.findOrThrowException(value, values());
    }

}
