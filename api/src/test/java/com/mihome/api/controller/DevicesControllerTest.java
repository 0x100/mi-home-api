package com.mihome.api.controller;


import com.mihome.api.core.enums.SlaveDeviceType;
import com.mihome.api.model.dto.SubscriptionData;
import com.mihome.api.service.DeviceService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DevicesControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private DeviceService deviceService;


    @Test
    public void getKnownDevices() {
        webTestClient
                .get().uri("/v1/devices")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();

        verify(deviceService).getKnownDevices();
    }

    @Test
    public void getDevicesByType() {
        webTestClient
                .get().uri("/v1/devices/type/" + SlaveDeviceType.XIAOMI_DOOR_WINDOW_SENSOR)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();

        verify(deviceService).getDevicesByType(SlaveDeviceType.XIAOMI_DOOR_WINDOW_SENSOR);
    }

    @Test
    public void subscribe() {
        SubscriptionData subscription = new SubscriptionData();
        subscription.setDeviceSid("112233445566");
        subscription.setWebHookUrl("http://localhost:8081/callback");

        webTestClient
                .post().uri("/v1/devices/subscribe")
                .body(BodyInserters.fromValue(subscription))
                .exchange()
                .expectStatus().isOk();

        verify(deviceService).subscribe(subscription);
    }
}