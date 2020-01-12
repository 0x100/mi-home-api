package com.mihome.api.core.command;

import com.google.gson.JsonObject;
import com.mihome.api.core.device.XiaomiGateway;

import java.nio.charset.StandardCharsets;

public class WriteSelfCommand extends AbstractCommand {
    private XiaomiGateway gateway;
    private JsonObject data;

    public WriteSelfCommand(XiaomiGateway gateway, JsonObject data, String key) {
        this.gateway = gateway;
        this.data = data;
        data.addProperty(Property.KEY, key);
    }

    @Override
    public byte[] toBytes() {
        String what = "{{\"cmd\":\"write\", \"sid\":\"" + gateway.getSid() + "\", \"data\":" + data + "}}";
        return what.getBytes(StandardCharsets.US_ASCII);
    }
}
