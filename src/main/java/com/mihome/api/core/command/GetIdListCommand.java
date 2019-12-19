package com.mihome.api.core.command;

import java.nio.charset.StandardCharsets;

public class GetIdListCommand extends AbstractCommand {
    public GetIdListCommand() {}

    @Override
    public byte[] toBytes() {
        return "{\"cmd\":\"get_id_list\"}".getBytes(StandardCharsets.US_ASCII);
    }
}
