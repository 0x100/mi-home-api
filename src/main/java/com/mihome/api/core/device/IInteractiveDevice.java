package com.mihome.api.core.device;

import java.util.Map;
import java.util.function.Consumer;

public interface IInteractiveDevice {
    class SubscriptionToken {}

    default SubscriptionToken subscribeForActions(Consumer<String> callback) {
        SubscriptionToken token = new SubscriptionToken();
        getActionsCallbacks().put(token, callback);
        return token;
    }

    default void unsubscribeForActions(SubscriptionToken token) {
        getActionsCallbacks().remove(token);
    }

    default void notifyWithAction(String action) {
        for(Consumer<String> c : getActionsCallbacks().values()) {
            c.accept(action);
        }
    }

    Map<SubscriptionToken, Consumer<String>> getActionsCallbacks();
}
