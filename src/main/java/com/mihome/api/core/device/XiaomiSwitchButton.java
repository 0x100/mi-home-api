package com.mihome.api.core.device;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mihome.api.core.enums.SlaveDeviceType;
import com.mihome.api.core.enums.SwitchButtonAction;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Slf4j
public class XiaomiSwitchButton extends SlaveDevice implements IInteractiveDevice {

    private SwitchButtonAction lastAction;
    private Map<SubscriptionToken, Consumer<String>> actionsCallbacks = new HashMap<>();

    XiaomiSwitchButton(XiaomiGateway gateway, String sid) {
        super(gateway, sid, SlaveDeviceType.XIAOMI_SWITCH_BUTTON);
    }

    @Override
    void update(String data) {
        try {
            JsonObject o = JSON_PARSER.parse(data).getAsJsonObject();
            if (o.has(Property.STATUS)) {
                updateWithAction(o.get(Property.STATUS).getAsString());
            }
        } catch (JsonSyntaxException e) {
            log.error("Update error", e);
        }
    }

    @Override
    public Map<SubscriptionToken, Consumer<String>> getActionsCallbacks() {
        return actionsCallbacks;
    }

    public SwitchButtonAction getLastAction() {
        return lastAction;
    }

    private void updateWithAction(String action) {
        lastAction = SwitchButtonAction.of(action);
        notifyWithAction(action);
    }
}
