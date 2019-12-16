package com.valashko.xaapi.device;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.valashko.xaapi.XaapiException;
import com.valashko.xaapi.channel.DirectChannel;
import com.valashko.xaapi.channel.IncomingMulticastChannel;
import com.valashko.xaapi.command.GetIdListCommand;
import com.valashko.xaapi.command.ReadCommand;
import com.valashko.xaapi.command.WhoisCommand;
import com.valashko.xaapi.command.WriteCommand;
import com.valashko.xaapi.command.WriteSelfCommand;
import com.valashko.xaapi.reply.GatewayHeartbeat;
import com.valashko.xaapi.reply.GetIdListReply;
import com.valashko.xaapi.reply.ReadReply;
import com.valashko.xaapi.reply.Reply;
import com.valashko.xaapi.reply.Report;
import com.valashko.xaapi.reply.SlaveDeviceHeartbeat;
import com.valashko.xaapi.reply.WhoisReply;
import lombok.extern.log4j.Log4j2;

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
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.stream.Stream;

@Log4j2
public class XiaomiGateway {

    enum DeviceModel {
        CUBE("cube"),
        MAGNET("magnet"),
        PLUG("plug"),
        MOTION("motion"),
        SWITCH("switch");

        private String value;

        DeviceModel(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        static DeviceModel of(String value) {
            return Stream.of(values())
                    .filter(m -> value.equals(m.value))
                    .findFirst()
                    .orElse(null);
        }
    }

    enum Command {
        REPORT("report"),
        HEARTBEAT("heartbeat");

        private String value;

        Command(String value) {
            this.value = value;
        }

        static Command of(String value) {
            return Stream.of(values())
                    .filter(a -> value.equals(a.value))
                    .findFirst()
                    .orElse(null);
        }
    }

    private static final String GROUP = "224.0.0.50";
    private static final int PORT = 9898;
    private static final int PORT_DISCOVERY = 4321;
    private static final byte[] IV =
            {0x17, (byte) 0x99, 0x6d, 0x09, 0x3d, 0x28, (byte) 0xdd, (byte) 0xb3,
                    (byte) 0xba, 0x69, 0x5a, 0x2e, 0x6f, 0x58, 0x56, 0x2e};

    private static final Gson GSON = new Gson();

    private String sid;
    private Optional<String> key = Optional.empty();
    private Cipher cipher;
    private IncomingMulticastChannel incomingMulticastChannel;
    private DirectChannel directChannel;
    private XiaomiGatewayLight builtinLight;
    private XiaomiGatewayIlluminationSensor builtinIlluminationSensor;
    private Map<String, SlaveDevice> knownDevices = new HashMap<>();
    private boolean continueReceivingUpdates;

    public static XiaomiGateway discover() throws IOException, XaapiException {
        // TODO discover more than one gateway
        DirectChannel discoveryChannel = new DirectChannel(GROUP, PORT_DISCOVERY);
        discoveryChannel.send(new WhoisCommand().toBytes());
        String replyString = new String(discoveryChannel.receive());
        WhoisReply reply = GSON.fromJson(replyString, WhoisReply.class);
        if (Integer.parseInt(reply.port) != PORT) {
            throw new XaapiException("Gateway occupies unexpected port: " + reply.port);
        }
        return new XiaomiGateway(reply.ip);
    }

    public XiaomiGateway(String ip) throws IOException, XaapiException {
        this.incomingMulticastChannel = new IncomingMulticastChannel(GROUP, PORT);
        this.directChannel = new DirectChannel(ip, PORT);
        queryDevices();
        configureBuiltinDevices();
    }

    public XiaomiGateway(String ip, String password) throws IOException, XaapiException {
        this(ip);
        configureCipher(password);
    }

    public void configurePassword(String password) throws XaapiException {
        configureCipher(password);
    }

    public Map<String, SlaveDevice> getKnownDevices() {
        return knownDevices;
    }

    private void configureBuiltinDevices() {
        builtinLight = new XiaomiGatewayLight(this);
        builtinIlluminationSensor = new XiaomiGatewayIlluminationSensor(this);
    }

    private void configureCipher(String password) throws XaapiException {
        try {
            cipher = Cipher.getInstance("AES/CBC/NoPadding");
            final SecretKeySpec keySpec = new SecretKeySpec(password.getBytes(), "AES");
            final IvParameterSpec ivSpec = new IvParameterSpec(IV);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | InvalidKeyException e) {
            throw new XaapiException("Cipher error: " + e.getMessage());
        }
    }

    private void queryDevices() throws XaapiException {
        try {
            directChannel.send(new GetIdListCommand().toBytes());
            String replyString = new String(directChannel.receive());
            GetIdListReply reply = GSON.fromJson(replyString, GetIdListReply.class);
            sid = reply.sid;
            for (String sid : GSON.fromJson(reply.data, String[].class)) {
                knownDevices.put(sid, readDevice(sid));
            }
        } catch (IOException e) {
            throw new XaapiException("Unable to query devices: " + e.getMessage());
        }
    }

    private SlaveDevice getDevice(String sid) {
        SlaveDevice device = knownDevices.get(sid);
        assert (device.getSid().equals(sid));
        return device;
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

    private void updateKey(String token) throws XaapiException {
        if (cipher != null) {
            try {
                String keyAsHexString = Utility.toHexString(cipher.doFinal(token.getBytes(StandardCharsets.US_ASCII)));
                key = Optional.of(keyAsHexString);
            } catch (IllegalBlockSizeException | BadPaddingException e) {
                throw new XaapiException("Cipher error: " + e.getMessage());
            }
        } else {
            throw new XaapiException("Unable to update key without a cipher. Did you forget to set a password?");
        }
    }

    void sendDataToDevice(SlaveDevice device, JsonObject data) throws XaapiException {
        if (key.isPresent()) {
            try {
                directChannel.send(new WriteCommand(device, data, key.get()).toBytes());
                // TODO add handling for expired key
            } catch (IOException e) {
                throw new XaapiException("Network error: " + e.getMessage());
            }
        } else {
            throw new XaapiException("Unable to control device without a key. Did you forget to set a password?");
        }
    }

    void sendDataToDevice(BuiltinDevice device /* just a type marker for overloading */, JsonObject data) throws XaapiException {
        assert device.gateway.equals(this);
        if (key.isPresent()) {
            try {
                directChannel.send(new WriteSelfCommand(this, data, key.get()).toBytes());
                // TODO add handling for expired key
            } catch (IOException e) {
                throw new XaapiException("Network error: " + e.getMessage());
            }
        } else {
            throw new XaapiException("Unable to control device without a key. Did you forget to set a password?");
        }
    }

    private SlaveDevice readDevice(String sid) throws XaapiException {
        try {
            directChannel.send(new ReadCommand(sid).toBytes());
            String replyString = new String(directChannel.receive());
            ReadReply reply = GSON.fromJson(replyString, ReadReply.class);
            DeviceModel model = DeviceModel.of(reply.model);

            SlaveDevice device = makeDevice(sid, model);
            device.update(reply.data);

            return device;
        } catch (IOException e) {
            throw new XaapiException("Unable to query device " + sid + ": " + e.getMessage());
        }
    }

    private SlaveDevice makeDevice(String sid, DeviceModel model) throws XaapiException {
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
                throw new XaapiException("Unsupported device model: " + model.getValue());
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
                } catch (IOException | XaapiException e) {
                    log.error("Update error", e);
                    continueReceivingUpdates = false;
                }
            }
        });
    }

    public void stopReceivingUpdates() {
        continueReceivingUpdates = false;
    }

    private void handleUpdate(Reply update, String received) throws XaapiException {
        switch (Command.of(update.cmd)) {
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
                throw new XaapiException("Unexpected update command: " + update.cmd);
        }
    }

    private void handleReport(Report report) {
        getDevice(report.sid).update(report.data);
    }

    private void handleBuiltinReport(Report report) {
        builtinLight.update(report.data);
        builtinIlluminationSensor.update(report.data);
    }

    private void handleGatewayHeartbeat(GatewayHeartbeat gatewayHeartbeat) throws XaapiException {
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