package com.mihome.api.core.device;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mihome.api.core.ApiException;
import com.mihome.api.core.enums.CubeAction;
import com.mihome.api.core.enums.SlaveDeviceType;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

@Slf4j
public class XiaomiCube extends SlaveDevice implements IInteractiveDevice {

    private int charge;
    private CubeAction lastAction;
    private Optional<Double> lastRotationAngle = Optional.empty();
    private Map<SubscriptionToken, Consumer<String>> actionsCallbacks = new HashMap<>();
    private Map<SubscriptionToken, Consumer<Double>> rotationCallbacks = new HashMap<>();

    public XiaomiCube(XiaomiGateway gateway, String sid) {
        super(gateway, sid, SlaveDeviceType.XIAOMI_CUBE);
    }

    @Override
    void update(String data) {
        try {
            JsonObject o = JSON_PARSER.parse(data).getAsJsonObject();
            if (o.has(Property.STATUS)) {
                updateWithAction(o.get(Property.STATUS).getAsString());
                resetLastRotationValue();
            }
            if (o.has(CubeAction.ROTATE.getValue())) {
                String angle = o.get(CubeAction.ROTATE.getValue()).getAsString().replace(',', '.'); // for some reason they use comma as decimal point
                updateWithRotation(Double.parseDouble(angle));
            }
        } catch (JsonSyntaxException e) {
            log.error("Update error", e);
        }
    }

    @Override
    public Map<SubscriptionToken, Consumer<String>> getActionsCallbacks() {
        return actionsCallbacks;
    }

    // battery charge in percent between 0 and 100
    public int getCharge() {
        return charge;
    }

    public CubeAction getLastAction() {
        return lastAction;
    }

    public double getLastRotationAngle() {
        if (lastRotationAngle.isPresent()) {
            return lastRotationAngle.get();
        } else {
            throw new ApiException("Last rotation value does not exist");
        }
    }

    public SubscriptionToken subscribeForRotation(Consumer<Double> callback) {
        SubscriptionToken token = new SubscriptionToken();
        rotationCallbacks.put(token, callback);
        return token;
    }

    public void unsubscribeForRotation(SubscriptionToken token) {
        rotationCallbacks.remove(token);
    }

    private void updateWithAction(String action) {
        lastAction = CubeAction.of(action);
        notifyWithAction(action);
    }

    private void updateWithRotation(double value) {
        lastAction = CubeAction.ROTATE;
        lastRotationAngle = Optional.of(value);
        notifyWithAction(CubeAction.ROTATE.getValue());
        notifyWithRotation(value);
    }

    private void resetLastRotationValue() {
        lastRotationAngle = Optional.empty();
    }

    private void notifyWithRotation(double value) {
        for (Consumer<Double> c : rotationCallbacks.values()) {
            c.accept(value);
        }
    }
}
