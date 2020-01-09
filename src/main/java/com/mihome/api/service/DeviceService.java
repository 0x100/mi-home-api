package com.mihome.api.service;

import com.mihome.api.core.device.IInteractiveDevice;
import com.mihome.api.core.device.SlaveDevice;
import com.mihome.api.core.device.XiaomiGateway;
import com.mihome.api.core.enums.SlaveDeviceType;
import com.mihome.api.model.dto.EventData;
import com.mihome.api.model.dto.SubscriptionData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.Map;

import static java.util.Optional.ofNullable;

@Service
@RequiredArgsConstructor
public class DeviceService {
    private final XiaomiGateway gateway;
    private final WebClient webClient;

    public Flux<SlaveDevice> getKnownDevices() {
        return ofNullable(gateway.getKnownDevices())
                .map(Map::values)
                .map(ArrayList::new)
                .map(Flux::fromIterable)
                .orElse(Flux.empty());
    }

    public Flux<SlaveDevice> getDevicesByType(SlaveDeviceType type) {
        return ofNullable(gateway.getDevicesByType(type))
                .map(Flux::fromIterable)
                .orElse(Flux.empty());
    }

    public void subscribe(SubscriptionData subscriptionData) {
        String sid = subscriptionData.getDeviceSid();
        SlaveDevice device = gateway.getKnownDevices().get(sid);

        if (device == null) {
            throw new IllegalArgumentException("Wrong device id");
        }
        if (device instanceof IInteractiveDevice) {
            IInteractiveDevice interactiveDevice = (IInteractiveDevice) device;
            interactiveDevice.subscribeForActions(action ->
                    webClient
                            .post()
                            .uri(subscriptionData.getWebHookUrl())
                            .body(BodyInserters.fromValue(new EventData(subscriptionData.getDeviceSid(), action)))
                            .exchange()
            );
        } else {
            throw new IllegalArgumentException("Cannot subscribe on the device");
        }
    }
}
