package com.valashko.xaapi.samples;

import com.valashko.xaapi.XaapiException;
import com.valashko.xaapi.device.XiaomiGateway;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.util.Collections.emptyMap;
import static java.util.Optional.ofNullable;

public class Example1 {

    public static void main(String[] args) throws IOException, XaapiException {
        XiaomiGateway gateway = new XiaomiGateway("192.168.1.33");
        System.out.printf("Gateway: %s\n", gateway.getSid() != null ? "found" : "not found");
        System.out.printf("Known devices: %s\n", ofNullable(gateway.getKnownDevices()).orElse(emptyMap()).values());
    }
}
