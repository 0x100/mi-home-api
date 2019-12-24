package com.mihome.api.core.device;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mihome.api.core.ApiException;
import com.mihome.api.core.channel.DirectChannel;
import com.mihome.api.core.channel.IncomingMulticastChannel;
import com.mihome.api.core.command.GetIdListCommand;
import com.mihome.api.core.command.ReadCommand;
import com.mihome.api.core.command.WhoisCommand;
import com.mihome.api.core.command.WriteCommand;
import com.mihome.api.core.command.WriteSelfCommand;
import com.mihome.api.core.enums.Command;
import com.mihome.api.core.enums.DeviceModel;
import com.mihome.api.core.reply.GatewayHeartbeat;
import com.mihome.api.core.reply.GetIdListReply;
import com.mihome.api.core.reply.ReadReply;
import com.mihome.api.core.reply.Reply;
import com.mihome.api.core.reply.Report;
import com.mihome.api.core.reply.SlaveDeviceHeartbeat;
import com.mihome.api.core.reply.WhoisReply;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class XiaomiGateway {
    private static final String GROUP = "224.0.0.50";
    private static final int PORT = 9898;
    private static final int PORT_DISCOVERY = 4321;
    private static final byte[] IV =
            {0x17, (byte) 0x99, 0x6d, 0x09, 0x3d, 0x28, (byte) 0xdd, (byte) 0xb3,
                    (byte) 0xba, 0x69, 0x5a, 0x2e, 0x6f, 0x58, 0x56, 0x2e};

    private static final Gson GSON = new Gson();

    private String sid;
    private String token;
    private Optional<String> key = Optional.empty();
    private Cipher cipher;
    private IncomingMulticastChannel incomingMulticastChannel;
    private DirectChannel directChannel;
    private XiaomiGatewayLight builtinLight;
    private XiaomiGatewayIlluminationSensor builtinIlluminationSensor;
    private Map<String, SlaveDevice> knownDevices = new HashMap<>();
    private boolean continueReceivingUpdates;


    // TODO discover more than one gateway
    public static XiaomiGateway discover() {
        try {
            DirectChannel discoveryChannel = new DirectChannel(GROUP, PORT_DISCOVERY);
            discoveryChannel.send(new WhoisCommand().toBytes());
            String replyString = new String(discoveryChannel.receive());
            WhoisReply reply = GSON.fromJson(replyString, WhoisReply.class);
            if (Integer.parseInt(reply.port) != PORT) {
                throw new ApiException("Gateway occupies unexpected port: " + reply.port);
            }
            return new XiaomiGateway(reply.ip);
        } catch (IOException ex) {
            log.error("error occurred while GW discover", ex);
            throw new ApiException(ex);
        }
    }

    public XiaomiGateway(String ip) throws IOException {
        this.incomingMulticastChannel = new IncomingMulticastChannel(GROUP, PORT);
        this.directChannel = new DirectChannel(ip, PORT);
        queryDevices();
        configureBuiltinDevices();
    }

    public XiaomiGateway(String ip, String password) throws IOException {
        this(ip);
        configureCipher(password);
        updateKey(token);
    }

    public void configurePassword(String password) {
        configureCipher(password);
    }

    public Map<String, SlaveDevice> getKnownDevices() {
        return knownDevices;
    }

    private void configureBuiltinDevices() {
        builtinLight = new XiaomiGatewayLight(this);
        builtinIlluminationSensor = new XiaomiGatewayIlluminationSensor(this);
    }

    private void configureCipher(String password) {
        try {
            cipher = Cipher.getInstance("AES/CBC/NoPadding");
            final SecretKeySpec keySpec = new SecretKeySpec(password.getBytes(), "AES");
            final IvParameterSpec ivSpec = new IvParameterSpec(IV);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | InvalidKeyException e) {
            throw new ApiException("Cipher error: " + e.getMessage());
        }
    }

    private void queryDevices() {
        try {
            directChannel.send(new GetIdListCommand().toBytes());
            String replyString = new String(directChannel.receive());
            GetIdListReply reply = GSON.fromJson(replyString, GetIdListReply.class);
            sid = reply.sid;
            token = reply.token;
            for (String sid : GSON.fromJson(reply.data, String[].class)) {
                knownDevices.put(sid, readDevice(sid));
            }
        } catch (IOException e) {
            throw new ApiException("Unable to query devices: " + e.getMessage());
        }
    }

    private SlaveDevice getDevice(String sid) {
        SlaveDevice device = knownDevices.get(sid);
        assert (device.getSid().equals(sid));
        return device;
    }

    public List<SlaveDevice> getDevicesByType(SlaveDevice.Type deviceType) {
        return knownDevices.values().stream()
                .filter(slaveDevice -> slaveDevice.getType() == deviceType)
                .collect(Collectors.toList());
    }

    public String getSid() {
        return sid;
    }

    public XiaomiGatewayLight getBuiltinLight() {
        return builtinLight;
    }

    public XiaomiGatewayIlluminationSensor getBuiltinIlluminationSensor() {
        return builtinIlluminationSensor;
    }

    private boolean isMyself(String sid) {
        return sid.equals(this.sid);
    }

    private void updateKey(String token) {
        if (cipher != null) {
            try {
                String keyAsHexString = Utility.toHexString(cipher.doFinal(token.getBytes(StandardCharsets.US_ASCII)));
                key = Optional.of(keyAsHexString);
            } catch (IllegalBlockSizeException | BadPaddingException e) {
                throw new ApiException("Cipher error: " + e.getMessage());
            }
        } else {
            throw new ApiException("Unable to update key without a cipher. Did you forget to set a password?");
        }
    }

    void sendDataToDevice(SlaveDevice device, JsonObject data) {
        if (key.isPresent()) {
            try {
                directChannel.send(new WriteCommand(device, data, key.get()).toBytes());
                // TODO add handling for expired key
            } catch (IOException e) {
                throw new ApiException("Network error: " + e.getMessage());
            }
        } else {
            throw new ApiException("Unable to control device without a key. Did you forget to set a password?");
        }
    }

    void sendDataToDevice(BuiltinDevice device /* just a type marker for overloading */, JsonObject data) {
        assert device.gateway.equals(this);
        if (key.isPresent()) {
            try {
                directChannel.send(new WriteSelfCommand(this, data, key.get()).toBytes());
                // TODO add handling for expired key
            } catch (IOException e) {
                throw new ApiException("Network error: " + e.getMessage());
            }
        } else {
            throw new ApiException("Unable to control device without a key. Did you forget to set a password?");
        }
    }

    private SlaveDevice readDevice(String sid) {
        try {
            directChannel.send(new ReadCommand(sid).toBytes());
            String replyString = new String(directChannel.receive());
            ReadReply reply = GSON.fromJson(replyString, ReadReply.class);
            DeviceModel model = DeviceModel.of(reply.model);

            SlaveDevice device = makeDevice(sid, model);
            device.update(reply.data);

            return device;
        } catch (IOException e) {
            throw new ApiException("Unable to query device " + sid + ": " + e.getMessage());
        }
    }

    private SlaveDevice makeDevice(String sid, DeviceModel model) {
        switch (model) {
            case CUBE:
                return new XiaomiCube(this, sid);
            case MAGNET:
                return new XiaomiDoorWindowSensor(this, sid);
            case PLUG:
                return new XiaomiSocket(this, sid);
            case MOTION:
                return new XiaomiMotionSensor(this, sid);
            case SWITCH:
                return new XiaomiSwitchButton(this, sid);
            default:
                throw new ApiException("Unsupported device model: " + model.getValue());
        }
    }

    public void startReceivingUpdates(Executor executor) {
        continueReceivingUpdates = true;
        executor.execute(() -> {
            while (continueReceivingUpdates) {
                try {
                    String received = new String(incomingMulticastChannel.receive());
                    handleUpdate(GSON.fromJson(received, ReadReply.class), received);
                } catch (SocketTimeoutException e) {
                    // ignore
                } catch (IOException e) {
                    log.error("Update error", e);
                    continueReceivingUpdates = false;
                }
            }
        });
    }

    public void stopReceivingUpdates() {
        continueReceivingUpdates = false;
    }

    private void handleUpdate(Reply update, String received) {
        Command command = Command.of(update.cmd);
        switch (command) {
            case REPORT:
                Report report = GSON.fromJson(received, Report.class);
                if (isMyself(update.sid)) {
                    handleBuiltinReport(report);
                } else {
                    handleReport(report);
                }
                break;
            case HEARTBEAT:
                if (isMyself(update.sid)) {
                    GatewayHeartbeat gatewayHeartbeat = GSON.fromJson(received, GatewayHeartbeat.class);
                    handleGatewayHeartbeat(gatewayHeartbeat);
                } else {
                    SlaveDeviceHeartbeat slaveDeviceHeartbeat = GSON.fromJson(received, SlaveDeviceHeartbeat.class);
                    handleSlaveDeviceHeartbeat(slaveDeviceHeartbeat);
                }
                break;
            default:
                throw new ApiException("Unexpected update command: " + update.cmd);
        }
    }

    private void handleReport(Report report) {
        getDevice(report.sid).update(report.data);
    }

    private void handleBuiltinReport(Report report) {
        builtinLight.update(report.data);
        builtinIlluminationSensor.update(report.data);
    }

    private void handleGatewayHeartbeat(GatewayHeartbeat gatewayHeartbeat) {
        if (cipher != null) {
            updateKey(gatewayHeartbeat.token);
        }
    }

    private void handleSlaveDeviceHeartbeat(SlaveDeviceHeartbeat slaveDeviceHeartbeat) {
        // TODO implement
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        XiaomiGateway that = (XiaomiGateway) o;
        return Objects.equals(sid, that.sid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sid);
    }
}