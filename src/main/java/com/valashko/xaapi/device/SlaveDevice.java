package com.valashko.xaapi.device;

import com.google.gson.JsonParser;
import com.valashko.xaapi.XaapiException;
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

    public XiaomiCube asXiaomiCube() throws XaapiException {
        ensureType(Type.XIAOMI_CUBE);
        return (XiaomiCube) this;
    }

    public XiaomiDoorWindowSensor asXiaomiDoorWindowSensor() throws XaapiException {
        ensureType(Type.XIAOMI_DOOR_WINDOW_SENSOR);
        return (XiaomiDoorWindowSensor) this;
    }

    public XiaomiSocket asXiaomiSocket() throws XaapiException {
        ensureType(Type.XIAOMI_SOCKET);
        return (XiaomiSocket) this;
    }

    public XiaomiMotionSensor asXiaomiMotionSensor() throws XaapiException {
        ensureType(Type.XIAOMI_MOTION_SENSOR);
        return (XiaomiMotionSensor) this;
    }

    public XiaomiSwitchButton asXiaomiSwitchButton() throws XaapiException {
        ensureType(Type.XIAOMI_SWITCH_BUTTON);
        return (XiaomiSwitchButton) this;
    }

    private void ensureType(Type type) throws XaapiException {
        if (getType() != type) {
            throw new XaapiException("Device type mismatch. Expected " + type);
        }
    }

    @Override
    public String toString() {
        return type.name();
    }
}
