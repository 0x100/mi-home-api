package com.mihome.api.core.device;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mihome.api.core.enums.DoorWindowSensorAction;
import com.mihome.api.core.enums.SlaveDeviceType;
import lombok.extern.log4j.Log4j2;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Log4j2
public class XiaomiDoorWindowSensor extends SlaveDevice implements IInteractiveDevice {

    private DoorWindowSensorAction lastAction;
    private Map<SubscriptionToken, Consumer<String>> actionsCallbacks = new HashMap<>();

    XiaomiDoorWindowSensor(XiaomiGateway gateway, String sid) {
        super(gateway, sid, SlaveDeviceType.XIAOMI_DOOR_WINDOW_SENSOR);
    }

    @Override
    void update(String data) {
        try {
            JsonObject o = JSON_PARSER.parse(data).getAsJsonObject();
            if (o.has(Property.STATUS)) {
                String action = o.get(Property.STATUS).getAsString();
                lastAction = DoorWindowSensorAction.of(action);
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

    public DoorWindowSensorAction getLastAction() {
        return lastAction;
    }
}
