package com.mihome.api.core.device;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mihome.api.core.enums.MotionSensorAction;
import com.mihome.api.core.enums.SlaveDeviceType;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class XiaomiMotionSensor extends SlaveDevice implements IInteractiveDevice {

    private MotionSensorAction lastAction;
    private Map<SubscriptionToken, Consumer<String>> actionsCallbacks = new HashMap<>();
    private Map<SubscriptionToken, Runnable> motionCallbacks = new HashMap<>();

    XiaomiMotionSensor(XiaomiGateway gateway, String sid) {
        super(gateway, sid, SlaveDeviceType.XIAOMI_MOTION_SENSOR);
    }

    @Override
    void update(String data) {
        try {
            JsonObject o = JSON_PARSER.parse(data).getAsJsonObject();
            if (o.has(Property.STATUS)) {
                String action = o.get(Property.STATUS).getAsString();
                lastAction = MotionSensorAction.of(action);
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

    public MotionSensorAction getLastAction() {
        return lastAction;
    }

    public SubscriptionToken subscribeForMotion(Runnable callback) {
        SubscriptionToken token = new SubscriptionToken();
        motionCallbacks.put(token, callback);
        return token;
    }

    public void unsubscribeForMotion(SubscriptionToken token) {
        motionCallbacks.remove(token);
    }

    private void notifyWithMotion() {
        for(Runnable r : motionCallbacks.values()) {
            r.run();
        }
    }
}
