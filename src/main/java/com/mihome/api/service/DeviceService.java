package com.mihome.api.service;

import com.mihome.api.core.device.SlaveDevice;
import com.mihome.api.core.device.XiaomiGateway;
import com.mihome.api.core.enums.SlaveDeviceType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Optional.ofNullable;

@Service
@RequiredArgsConstructor
public class DeviceService {

    private final XiaomiGateway gateway;

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
}
