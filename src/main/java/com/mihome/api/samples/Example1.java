package com.mihome.api.samples;

import com.mihome.api.core.device.XiaomiGateway;
import com.mihome.api.core.enums.SlaveDeviceType;

import java.io.IOException;

public class Example1 {

    public static void main(String[] args) throws IOException {
        XiaomiGateway gateway = new XiaomiGateway("192.168.1.123");
        System.out.println("Gateway sid: " + gateway.getSid());
        System.out.println("Known devices:");
        gateway.getKnownDevices().forEach((sid, device) -> {
            SlaveDeviceType deviceType = device.getType();
            System.out.printf("\t %s, sid: %s", deviceType, sid);
        });
    }
}
