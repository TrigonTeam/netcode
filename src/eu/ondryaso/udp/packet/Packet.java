package eu.ondryaso.udp.packet;

import eu.ondryaso.udp.Netcode;

import java.net.SocketAddress;

public abstract class Packet {
    protected boolean hasFinished = false;
    protected Netcode server;
    protected SocketAddress address;

    public Packet(Netcode server, SocketAddress address) {
        this.server = server;
        this.address = address;
    }

    public SocketAddress getAddress() {
        return this.address;
    }

    public boolean hasFinishedProcessing() {
        return this.hasFinished;
    }

    public abstract void useIncoming();
    public abstract void processIncoming(byte[] bytes);
    public abstract byte[] processOutgoing();
}
