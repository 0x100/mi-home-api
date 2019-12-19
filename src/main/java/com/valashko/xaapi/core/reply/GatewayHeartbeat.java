package com.valashko.xaapi.core.reply;

public class GatewayHeartbeat extends Reply {
    public String model;
    public String shortId; // NB: sometimes it is a string and sometimes a number
    public String token;
    public String data;
}
