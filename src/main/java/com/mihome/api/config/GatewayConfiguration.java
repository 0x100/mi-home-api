package com.mihome.api.config;

import com.mihome.api.core.device.XiaomiGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfiguration {

    @Bean
    public XiaomiGateway gateway() {
        return XiaomiGateway.discover();
    }
}
