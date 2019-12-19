package com.valashko.xaapi.core.device;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class Utility {

    static String toHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

    public static byte[] makeResponse(DatagramSocket socket) throws IOException {
        byte[] buffer = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet);
        int responseLength = packet.getLength();
        byte[] response = new byte[responseLength];
        System.arraycopy(buffer, 0, response, 0, responseLength);
        return response;
    }
}
