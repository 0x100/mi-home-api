package com.mihome.api.service;

import com.mihome.api.core.device.SlaveDevice;
import com.mihome.api.core.device.XiaomiGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Optional.ofNullable;

@Service
@RequiredArgsConstructor
public class GatewayService {
    private final XiaomiGateway gateway;

    public List<SlaveDevice> getKnownDevices() {
        return ofNullable(gateway.getKnownDevices())
                .map(Map::values)
                .map(ArrayList::new)
                .orElse(new ArrayList<>());
    }
}
