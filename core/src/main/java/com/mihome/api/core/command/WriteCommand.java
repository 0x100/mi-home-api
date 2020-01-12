package com.mihome.api.core.command;

import com.google.gson.JsonObject;
import com.mihome.api.core.device.SlaveDevice;

import java.nio.charset.StandardCharsets;

public class WriteCommand extends AbstractCommand {
    private SlaveDevice device;
    private JsonObject data;

    public WriteCommand(SlaveDevice device, JsonObject data, String key) {
        this.device = device;
        this.data = data;
        data.addProperty(Property.KEY, key);
    }

    @Override
    public byte[] toBytes() {
        String what = "{{\"cmd\":\"write\", \"sid\":\"" + device.getSid() + "\", \"data\":" + data + "}}";
        return what.getBytes(StandardCharsets.US_ASCII);
    }
}
