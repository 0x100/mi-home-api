package com.mihome.api.service;

import com.mihome.api.core.device.SlaveDevice;
import com.mihome.api.core.device.XiaomiDoorWindowSensor;
import com.mihome.api.core.device.XiaomiGateway;
import com.mihome.api.model.dto.SubscriptionData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
class DeviceServiceTest {

    @Mock
    private XiaomiGateway gateway;

    @Spy
    private WebClient webClient = WebClient.create();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void subscribe() {
        final String deviceSid = "112233445566";
        XiaomiDoorWindowSensor doorWindowSensor = new XiaomiDoorWindowSensor(gateway, deviceSid);

        Map<String, SlaveDevice> knownDevices = new HashMap<String, SlaveDevice>() {{
            put(deviceSid, doorWindowSensor);
        }};
        when(gateway.getKnownDevices()).thenReturn(knownDevices);

        SubscriptionData subscription = new SubscriptionData();
        subscription.setDeviceSid(deviceSid);
        subscription.setWebHookUrl("http://localhost:8081/callback/" + deviceSid);

        DeviceService deviceService = spy(new DeviceService(gateway, webClient));
        deviceService.subscribe(subscription);
        doorWindowSensor.asXiaomiDoorWindowSensor().notifyWithAction("opened");

        verify(webClient).post();
    }
}