package com.mihome.api.core.device;

import com.google.gson.JsonParser;
import com.mihome.api.core.ApiException;
import org.apache.commons.lang3.NotImplementedException;

public abstract class SlaveDevice {

    public enum Type {
        XIAOMI_CUBE,
        XIAOMI_DOOR_WINDOW_SENSOR,
        XIAOMI_SOCKET,
        XIAOMI_MOTION_SENSOR,
        XIAOMI_SWITCH_BUTTON
    }

    static class Property {
        static final String STATUS = "status";
    }

    static JsonParser JSON_PARSER = new JsonParser();
    protected XiaomiGateway gateway;
    private String sid;
    private Type type;

    SlaveDevice(XiaomiGateway gateway, String sid, Type type) {
        this.gateway = gateway;
        this.sid = sid;
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public String getSid() {
        return sid;
    }

    public short getShortId() {
        throw new NotImplementedException("Method is not implemented yet"); // TODO implement
    }

    abstract void update(String data);

    public XiaomiCube asXiaomiCube() {
        ensureType(Type.XIAOMI_CUBE);
        return (XiaomiCube) this;
    }

    public XiaomiDoorWindowSensor asXiaomiDoorWindowSensor() {
        ensureType(Type.XIAOMI_DOOR_WINDOW_SENSOR);
        return (XiaomiDoorWindowSensor) this;
    }

    public XiaomiSocket asXiaomiSocket() {
        ensureType(Type.XIAOMI_SOCKET);
        return (XiaomiSocket) this;
    }

    public XiaomiMotionSensor asXiaomiMotionSensor() {
        ensureType(Type.XIAOMI_MOTION_SENSOR);
        return (XiaomiMotionSensor) this;
    }

    public XiaomiSwitchButton asXiaomiSwitchButton() {
        ensureType(Type.XIAOMI_SWITCH_BUTTON);
        return (XiaomiSwitchButton) this;
    }

    private void ensureType(Type type) {
        if (getType() != type) {
            throw new ApiException("Device type mismatch. Expected " + type);
        }
    }
}
