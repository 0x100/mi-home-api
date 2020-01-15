package com.mihome.api.model.dto;

import lombok.Data;

@Data
public class SubscriptionData {
    private String deviceSid;
    /**
     * Callback url, calling when any event is fired on the subscribed device
     */
    private String webHookUrl;
}
