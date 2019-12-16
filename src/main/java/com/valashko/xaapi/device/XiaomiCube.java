package com.valashko.xaapi.device;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.valashko.xaapi.XaapiException;
import lombok.extern.log4j.Log4j2;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

@Log4j2
public class XiaomiCube extends SlaveDevice implements IInteractiveDevice {

    public enum Action {
        FLIP_90("flip90"),
        FLIP_180("flip180"),
        MOVE("move"),
        TAP_TWICE("tap_twice"),
        SHAKE("shake_air"),
        SWING("swing"),
        ALERT("alert"),
        FREE_FALL("free_fall"),
        ROTATE("rotate");

        private String value;

        Action(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        static Action of(String value) throws XaapiException {
            return Stream.of(values())
                    .filter(a -> value.equals(a.value))
                    .findFirst()
                    .orElseThrow(() -> new XaapiException("Unknown action: " + value));
        }
    }

    private int charge;
    private Action lastAction;
    private Optional<Double> lastRotationAngle = Optional.empty();
    private Map<SubscriptionToken, Consumer<String>> actionsCallbacks = new HashMap<>();
    private Map<SubscriptionToken, Consumer<Double>> rotationCallbacks = new HashMap<>();

    public XiaomiCube(XiaomiGateway gateway, String sid) {
        super(gateway, sid, Type.XIAOMI_CUBE);
    }

    @Override
    void update(String data) {
        try {
            JsonObject o = JSON_PARSER.parse(data).getAsJsonObject();
            if (o.has(Property.STATUS)) {
                updateWithAction(o.get(Property.STATUS).getAsString());
                resetLastRotationValue();
            }
            if (o.has(Action.ROTATE.getValue())) {
                String angle = o.get(Action.ROTATE.getValue()).getAsString().replace(',', '.'); // for some reason they use comma as decimal point
                updateWithRotation(Double.parseDouble(angle));
            }
        } catch (XaapiException | JsonSyntaxException e) {
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

    public Action getLastAction() {
        return lastAction;
    }

    public double getLastRotationAngle() throws XaapiException {
        if (lastRotationAngle.isPresent()) {
            return lastRotationAngle.get();
        } else {
            throw new XaapiException("Last rotation value does not exist");
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

    private void updateWithAction(String action) throws XaapiException {
        lastAction = Action.of(action);
        notifyWithAction(action);
    }

    private void updateWithRotation(double value) {
        lastAction = Action.ROTATE;
        lastRotationAngle = Optional.of(value);
        notifyWithAction(Action.ROTATE.getValue());
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
