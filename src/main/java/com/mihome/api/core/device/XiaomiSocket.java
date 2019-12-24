package com.mihome.api.core.device;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mihome.api.core.enums.DeviceAction;
import com.mihome.api.core.enums.SlaveDeviceType;
import lombok.extern.log4j.Log4j2;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Log4j2
public class XiaomiSocket extends SlaveDevice implements IInteractiveDevice {

    private DeviceAction lastAction;
    private Map<SubscriptionToken, Consumer<String>> actionsCallbacks = new HashMap<>();

    XiaomiSocket(XiaomiGateway gateway, String sid) {
        super(gateway, sid, SlaveDeviceType.XIAOMI_SOCKET);
    }

    @Override
    void update(String data) {
        try {
            JsonObject o = JSON_PARSER.parse(data).getAsJsonObject();
            if (o.has(Property.STATUS)) {
                String action = o.get(Property.STATUS).getAsString();
                lastAction = DeviceAction.of(action);
                notifyWithAction(action);
            }
        } catch (JsonSyntaxException e) {
            log.error("Update error", e);
        }
    }

    @Override
    public Map<SubscriptionToken, Consumer<String>> getActionsCallbacks() {
        return actionsCallbacks;
    }

    public DeviceAction getLastAction() {
        return lastAction;
    }

    public void turnOn() {
        JsonObject on = new JsonObject();
        on.addProperty(Property.STATUS, DeviceAction.ON.getValue());
        gateway.sendDataToDevice(this, on);
    }

    public void turnOff() {
        JsonObject off = new JsonObject();
        off.addProperty(Property.STATUS, DeviceAction.OFF.getValue());
        gateway.sendDataToDevice(this, off);
    }
}
