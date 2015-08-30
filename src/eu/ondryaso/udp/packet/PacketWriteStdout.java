package eu.ondryaso.udp.packet;

import eu.ondryaso.udp.Netcode;

import java.io.UnsupportedEncodingException;
import java.net.SocketAddress;

public class PacketWriteStdout extends Packet {
    private String inner;

    public PacketWriteStdout(Netcode server, SocketAddress address) {
        super(server, address);
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
