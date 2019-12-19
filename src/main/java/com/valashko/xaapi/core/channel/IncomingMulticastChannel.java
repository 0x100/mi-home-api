package com.valashko.xaapi.core.channel;

import com.valashko.xaapi.core.device.Utility;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class IncomingMulticastChannel {

    private static final int SOCKET_TIMEOUT = 1000; // milliseconds

    private MulticastSocket socket;

    public IncomingMulticastChannel(String group, int localPort) throws IOException {
        this.socket = new MulticastSocket(localPort);
        this.socket.setSoTimeout(SOCKET_TIMEOUT);
        // on OS X make sure to run with -Djava.net.preferIPv4Stack=true
        this.socket.joinGroup(InetAddress.getByName(group));
    }

    public byte[] receive() throws IOException {
        return Utility.makeResponse(socket);
    }
}
