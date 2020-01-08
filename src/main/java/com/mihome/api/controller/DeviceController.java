package com.mihome.api.controller;

import com.mihome.api.core.device.SlaveDevice;
import com.mihome.api.core.enums.SlaveDeviceType;
import com.mihome.api.service.DeviceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import static reactor.core.publisher.Flux.fromIterable;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/devices")
public class DeviceController {

    private final DeviceService deviceService;

    @GetMapping
    public Flux<SlaveDevice> getKnownDevices() {
        return fromIterable(deviceService.getKnownDevices());
    }

    @GetMapping("/type/{type}")
    public Flux<SlaveDevice> getKnownDevicesByType(@PathVariable SlaveDeviceType type) {
        return fromIterable(deviceService.getDevicesByType(type));
    }
}
