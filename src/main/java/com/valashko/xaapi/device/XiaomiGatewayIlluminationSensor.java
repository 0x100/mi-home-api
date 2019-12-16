package com.valashko.xaapi.device;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import lombok.extern.log4j.Log4j2;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Log4j2
public class XiaomiGatewayIlluminationSensor extends BuiltinDevice {

    static class Property {
        static final String ILLUMINATION = "illumination";
    }

    private int illumination;
    private Map<IInteractiveDevice.SubscriptionToken, Consumer<Integer>> illuminationChangeCallbacks = new HashMap<>();

    public XiaomiGatewayIlluminationSensor(XiaomiGateway gateway) {
        super(gateway, DeviceType.XIAOMI_GATEWAY_ILLUMINATION_SENSOR);
    }

    @Override
    void update(String data) {
        try {
            JsonObject o = JSON_PARSER.parse(data).getAsJsonObject();
            illumination = o.get(Property.ILLUMINATION).getAsInt();
            notifyWithIlluminationChange(illumination);
        } catch (JsonSyntaxException e) {
            log.error("Update error", e);
        }
    }

    public int getIllumination() {
        return illumination;
    }

    public IInteractiveDevice.SubscriptionToken subscribeForIlluminationChange(Consumer<Integer> callback) {
        IInteractiveDevice.SubscriptionToken token = new IInteractiveDevice.SubscriptionToken();
        illuminationChangeCallbacks.put(token, callback);
        return token;
    }

    public void unsubscribeForIlluminationChange(IInteractiveDevice.SubscriptionToken token) {
        illuminationChangeCallbacks.remove(token);
    }

    private void notifyWithIlluminationChange(int value) {
        for (Consumer<Integer> c : illuminationChangeCallbacks.values()) {
            c.accept(value);
        }
    }
}
