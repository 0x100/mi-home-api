package com.valashko.xaapi.core.device;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.valashko.xaapi.core.ApiException;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class XiaomiDoorWindowSensor extends SlaveDevice implements IInteractiveDevice {

    public enum Action {
        OPEN("open"),
        CLOSE("close");

        private String value;

        Action(String value) {
            this.value = value;
        }

        static Action of(String value) {
            return Stream.of(values())
                    .filter(a -> value.equals(a.value))
                    .findFirst()
                    .orElseThrow(() -> new ApiException("Unknown action: " + value));
        }
    }

    private Action lastAction;
    private Map<SubscriptionToken, Consumer<String>> actionsCallbacks = new HashMap<>();

    XiaomiDoorWindowSensor(XiaomiGateway gateway, String sid) {
        super(gateway, sid, Type.XIAOMI_DOOR_WINDOW_SENSOR);
    }

    @Override
    void update(String data) {
        try {
            JsonObject o = JSON_PARSER.parse(data).getAsJsonObject();
            if (o.has(Property.STATUS)) {
                String action = o.get(Property.STATUS).getAsString();
                lastAction = Action.of(action);
                notifyWithAction(action);
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Map<SubscriptionToken, Consumer<String>> getActionsCallbacks() {
        return actionsCallbacks;
    }

    public Action getLastAction() {
        return lastAction;
    }
}
