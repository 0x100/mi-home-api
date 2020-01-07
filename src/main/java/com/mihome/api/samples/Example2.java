package com.mihome.api.samples;

import com.mihome.api.core.device.SlaveDevice;
import com.mihome.api.core.device.XiaomiDoorWindowSensor;
import com.mihome.api.core.device.XiaomiGateway;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.mihome.api.core.enums.SlaveDeviceType.XIAOMI_DOOR_WINDOW_SENSOR;

public class Example2 {

    private static final int TERMINATION_TIMEOUT = 60; // Listening time in seconds

    public static void main(String[] args) {
        XiaomiGateway gateway = XiaomiGateway.discover();
        Optional<SlaveDevice> doorSensor = gateway.getDevicesByType(XIAOMI_DOOR_WINDOW_SENSOR).stream().findFirst();
        doorSensor.ifPresent(s -> {
            try {
                System.out.println("Door sensor sid: " + s.getSid());
                subscribe(s.asXiaomiDoorWindowSensor());
                listen(gateway);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private static void subscribe(XiaomiDoorWindowSensor doorSensor) {
        doorSensor.subscribeForActions(action ->
                System.out.println("Door sensor: " + action));
    }

    private static void listen(XiaomiGateway gateway) throws InterruptedException {
        ExecutorService threadPool = Executors.newSingleThreadExecutor();

        System.out.println("Started");
        gateway.startReceivingUpdates(threadPool);
        threadPool.awaitTermination(TERMINATION_TIMEOUT, TimeUnit.SECONDS); // run timeout

        gateway.stopReceivingUpdates();
        threadPool.shutdown();
        System.out.println("Stopped");
    }
}
