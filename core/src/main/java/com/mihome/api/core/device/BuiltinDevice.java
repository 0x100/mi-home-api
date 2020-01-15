package com.mihome.api.core.device;

import com.google.gson.JsonParser;
import com.mihome.api.core.ApiException;
import com.mihome.api.core.enums.DeviceType;

public abstract class BuiltinDevice {

    protected static JsonParser JSON_PARSER = new JsonParser();
    protected XiaomiGateway gateway;
    private String uid;
    private DeviceType deviceType;

    public BuiltinDevice(XiaomiGateway gateway, DeviceType deviceType) {
        this.gateway = gateway;
        this.uid = gateway.getSid() + ":" + deviceType.getValue();
        this.deviceType = deviceType;
    }

    public String getUid() {
        return uid;
    }

    public DeviceType getDeviceType() {
        return deviceType;
    }

    abstract void update(String data);

    public XiaomiGatewayLight asXiaomiGatewayLight() {
        ensureType(DeviceType.XIAOMI_GATEWAY_LIGHT);
        return (XiaomiGatewayLight) this;
    }

    public XiaomiGatewayIlluminationSensor asXiaomiGatewayIlluminationSensor() {
        ensureType(DeviceType.XIAOMI_GATEWAY_ILLUMINATION_SENSOR);
        return (XiaomiGatewayIlluminationSensor) this;
    }

    private void ensureType(DeviceType deviceType) {
        if (getDeviceType() != deviceType) {
            throw new ApiException("Device type mismatch. Expected " + deviceType);
        }
    }
}
