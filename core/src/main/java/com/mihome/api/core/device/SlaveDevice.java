package com.mihome.api.core.device;

import com.google.gson.JsonParser;
import com.mihome.api.core.ApiException;
import com.mihome.api.core.enums.SlaveDeviceType;
import org.apache.commons.lang3.NotImplementedException;

public abstract class SlaveDevice {

    static class Property {
        static final String STATUS = "status";
    }

    static JsonParser JSON_PARSER = new JsonParser();
    protected XiaomiGateway gateway;
    private String sid;
    private SlaveDeviceType type;

    SlaveDevice(XiaomiGateway gateway, String sid, SlaveDeviceType type) {
        this.gateway = gateway;
        this.sid = sid;
        this.type = type;
    }

    public SlaveDeviceType getType() {
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
        ensureType(SlaveDeviceType.XIAOMI_CUBE);
        return (XiaomiCube) this;
    }

    public XiaomiDoorWindowSensor asXiaomiDoorWindowSensor() {
        ensureType(SlaveDeviceType.XIAOMI_DOOR_WINDOW_SENSOR);
        return (XiaomiDoorWindowSensor) this;
    }

    public XiaomiSocket asXiaomiSocket() {
        ensureType(SlaveDeviceType.XIAOMI_SOCKET);
        return (XiaomiSocket) this;
    }

    public XiaomiMotionSensor asXiaomiMotionSensor() {
        ensureType(SlaveDeviceType.XIAOMI_MOTION_SENSOR);
        return (XiaomiMotionSensor) this;
    }

    public XiaomiSwitchButton asXiaomiSwitchButton() {
        ensureType(SlaveDeviceType.XIAOMI_SWITCH_BUTTON);
        return (XiaomiSwitchButton) this;
    }

    private void ensureType(SlaveDeviceType type) {
        if (getType() != type) {
            throw new ApiException("Device type mismatch. Expected " + type);
        }
    }
}
