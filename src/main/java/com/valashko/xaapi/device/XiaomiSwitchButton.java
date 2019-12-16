package com.valashko.xaapi.device;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.valashko.xaapi.XaapiException;
import lombok.extern.log4j.Log4j2;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

@Log4j2
public class XiaomiSwitchButton extends SlaveDevice implements IInteractiveDevice {

    public enum Action {
        CLICK("click"),
        DOUBLE_CLICK("double_click"),
        LONG_CLICK_PRESS("long_click_press"),
        LONG_CLICK_RELEASE("long_click_release");

        private String value;

        Action(String value) {
            this.value = value;
        }

        static Action of(String value) throws XaapiException {
            return Stream.of(values())
                    .filter(a -> value.equals(a.value))
                    .findFirst()
                    .orElseThrow(() -> new XaapiException("Unknown action: " + value));
        }
    }

    private Action lastAction;
    private Map<SubscriptionToken, Consumer<String>> actionsCallbacks = new HashMap<>();

    XiaomiSwitchButton(XiaomiGateway gateway, String sid) {
        super(gateway, sid, Type.XIAOMI_SWITCH_BUTTON);
    }

    @Override
    void update(String data) {
        try {
            JsonObject o = JSON_PARSER.parse(data).getAsJsonObject();
            if (o.has(Property.STATUS)) {
                updateWithAction(o.get(Property.STATUS).getAsString());
            }
        } catch (XaapiException | JsonSyntaxException e) {
            log.error("Update error", e);
        }
    }

    @Override
    public Map<SubscriptionToken, Consumer<String>> getActionsCallbacks() {
        return actionsCallbacks;
    }

    public Action getLastAction() {
        return lastAction;
    }

    private void updateWithAction(String action) throws XaapiException {
        lastAction = Action.of(action);
        notifyWithAction(action);
    }
}
