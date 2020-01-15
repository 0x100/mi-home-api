package com.mihome.api.controller;

import com.mihome.api.core.device.SlaveDevice;
import com.mihome.api.core.enums.SlaveDeviceType;
import com.mihome.api.model.dto.SubscriptionData;
import com.mihome.api.service.DeviceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/devices")
public class DeviceController {
    private final DeviceService deviceService;

    @GetMapping
    public Flux<SlaveDevice> getKnownDevices() {
        return deviceService.getKnownDevices();
    }

    @GetMapping("/type/{type}")
    public Flux<SlaveDevice> getDevicesByType(@PathVariable SlaveDeviceType type) {
        return deviceService.getDevicesByType(type);
    }

    @PostMapping("/subscribe")
    public void subscribe(@RequestBody SubscriptionData data) {
        deviceService.subscribe(data);
    }
}
