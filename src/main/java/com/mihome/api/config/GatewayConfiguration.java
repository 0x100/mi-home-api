package com.mihome.api.config;

import com.mihome.api.core.device.XiaomiGateway;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfiguration {
    public XiaomiGateway gateway() {
        XiaomiGateway discover = XiaomiGateway.discover();
        return discover;
    }
}
