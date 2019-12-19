package com.valashko.xaapi.core.command;

import java.nio.charset.StandardCharsets;

public class WhoisCommand extends AbstractCommand {

    @Override
    public byte[] toBytes() {
        return "{\"cmd\":\"whois\"}".getBytes(StandardCharsets.US_ASCII);
    }
}
