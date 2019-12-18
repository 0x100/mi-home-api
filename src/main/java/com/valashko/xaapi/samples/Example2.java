package com.valashko.xaapi.samples;

import com.valashko.xaapi.device.IInteractiveDevice.SubscriptionToken;
import com.valashko.xaapi.device.SlaveDevice;
import com.valashko.xaapi.device.XiaomiDoorWindowSensor;
import com.valashko.xaapi.device.XiaomiGateway;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Example2 {

    private static final int TERMINATION_TIMEOUT = 60; // in seconds

    public static void main(String[] args) throws Exception {
        XiaomiGateway gateway = new XiaomiGateway("192.168.1.123");

        Optional<SlaveDevice> doorSensor = gateway.getDevicesByType(SlaveDevice.Type.XIAOMI_DOOR_WINDOW_SENSOR).stream().findFirst();
        if (doorSensor.isPresent()) {
            System.out.println("Door sensor sid: " + doorSensor.get().getSid());

            SubscriptionToken subscriptionToken = subscribe(doorSensor.get().asXiaomiDoorWindowSensor());
            listen(gateway);

            unsubscribe(doorSensor.get().asXiaomiDoorWindowSensor(), subscriptionToken);
        } else {
            System.out.println("Device not found");
        }
    }

    private static SubscriptionToken subscribe(XiaomiDoorWindowSensor doorSensor) {
        return doorSensor.subscribeForActions(action ->
                System.out.println("Door sensor: " + action));
    }

    private static void unsubscribe(XiaomiDoorWindowSensor doorSensor, SubscriptionToken subscriptionToken) {
        doorSensor.unsubscribeForActions(subscriptionToken);
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
