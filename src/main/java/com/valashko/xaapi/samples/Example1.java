package com.valashko.xaapi.samples;

import com.valashko.xaapi.XaapiException;
import com.valashko.xaapi.device.SlaveDevice;
import com.valashko.xaapi.device.XiaomiGateway;

import java.io.IOException;

public class Example1 {

    public static void main(String[] args) throws IOException, XaapiException {
        XiaomiGateway gateway = new XiaomiGateway("192.168.1.123");
        System.out.println("Gateway sid: " + gateway.getSid());
        System.out.println("Known devices:");
        gateway.getKnownDevices().forEach((sid, device) -> {
            SlaveDevice.Type deviceType = device.getType();
            System.out.printf("\t %s, sid: %s", deviceType, sid);
        });
    }
}
