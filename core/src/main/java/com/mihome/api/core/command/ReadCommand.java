package com.mihome.api.core.command;

import java.nio.charset.StandardCharsets;

public class ReadCommand extends AbstractCommand {
    private String sid;

    public ReadCommand(String sid) {
        this.sid = sid;
    }

    @Override
    public byte[] toBytes() {
        return ("{\"cmd\":\"read\", \"sid\":\"" + sid + "\"}").getBytes(StandardCharsets.US_ASCII);
    }
}
