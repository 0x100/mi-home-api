package com.valashko.xaapi.samples;

import com.valashko.xaapi.XaapiException;
import com.valashko.xaapi.device.XiaomiGateway;
import com.valashko.xaapi.device.XiaomiGatewayLight;

import java.awt.*;
import java.io.IOException;

/**
 * Police lights
 *
 * You can find a gateway password here:
 * Mi Home App > Select the Gateway device > ... (dots at the top right of the screen) > About > Wireless communication protocol.
 * If the "Wireless communication protocol" option isn't shown, tap a plugin version number at the bottom of the screen several time.
 */
public class Example3 {
    private static final int SLEEP_TIMEOUT = 150;

    public static void main(String[] args) throws IOException, XaapiException, InterruptedException {
        String password = "a22b4b5b6c7cc0d";
        XiaomiGateway gateway = new XiaomiGateway("192.168.1.123", password);
        XiaomiGatewayLight gatewayLight = gateway.getBuiltinLight();

        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 3; j++) {
                gatewayLight.setColor(Color.RED);
                Thread.sleep(SLEEP_TIMEOUT);
                gatewayLight.setColor(Color.BLACK);
            }
            for (int j = 0; j < 3; j++) {
                gatewayLight.setColor(Color.BLUE);
                Thread.sleep(SLEEP_TIMEOUT);
                gatewayLight.setColor(Color.BLACK);
            }
        }
    }
}
