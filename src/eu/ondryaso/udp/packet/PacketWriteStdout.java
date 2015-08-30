package eu.ondryaso.udp.packet;

import eu.ondryaso.udp.Netcode;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;

public class PacketWriteStdout extends Packet {
    private String inner = "default";

    public PacketWriteStdout(Netcode server, InetAddress address, int port) {
        super(server, address, port);
    }

    public PacketWriteStdout(Netcode server, InetAddress address, int port, String str) {
        super(server, address, port);
        this.inner = str;
    }

    @Override
    public void useIncoming() {
        System.out.println(inner);
    }

    @Override
    public void processIncoming(byte[] bytes) {
        try {
            this.inner = new String(bytes, "UTF-8");
            this.hasFinished = true;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public byte[] processOutgoing() {
        try {
            return this.inner.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
