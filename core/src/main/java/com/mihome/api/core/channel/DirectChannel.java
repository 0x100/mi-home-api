package com.mihome.api.core.channel;

import com.mihome.api.core.device.Utility;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class DirectChannel {

    private static final int SOCKET_TIMEOUT = 1000; // milliseconds

    private String ip;
    private int port;
    private DatagramSocket socket;

    public DirectChannel(String destinationIp, int destinationPort) throws IOException {
        this.ip = destinationIp;
        this.port = destinationPort;
        this.socket = new DatagramSocket();
        this.socket.setSoTimeout(SOCKET_TIMEOUT);
    }

    public byte[] receive() throws IOException {
        return Utility.makeResponse(socket);
    }

    public void send(byte[] bytes) throws IOException {
        InetSocketAddress address = new InetSocketAddress(ip, port);
        DatagramPacket packet = new DatagramPacket(bytes, bytes.length, address);
        socket.send(packet);
    }

}
