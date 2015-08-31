package eu.ondryaso.udp.packet;

import com.esotericsoftware.kryonet.Connection;

import java.io.UnsupportedEncodingException;

public class PacketWriteStdout extends Packet {
    private String inner = "default";

    public PacketWriteStdout(Connection connection) {
        super(connection);
    }

    public PacketWriteStdout(Connection connection, String str) {
        super(connection);
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
