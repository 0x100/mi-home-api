package com.mihome.api.controller;

import com.mihome.api.core.device.SlaveDevice;
import com.mihome.api.service.GatewayService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/devices")
public class DeviceController {

    private final GatewayService gatewayService;

    @GetMapping
    public List<SlaveDevice> getKnownDevices() {
        return gatewayService.getKnownDevices();
    }
}
