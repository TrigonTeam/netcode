package eu.ondryaso.udp.packet;

import eu.ondryaso.udp.Netcode;

import java.net.InetAddress;

public abstract class Packet {
    protected boolean hasFinished = false;
    protected Netcode server;
    protected InetAddress address;
    protected int port;

    public Packet(Netcode server, InetAddress address, int port) {
        this.server = server;
        this.address = address;
        this.port = port;
    }

    public InetAddress getAddress() {
        return this.address;
    }

    public int getPort() {
        return this.port;
    }

    public boolean hasFinishedProcessing() {
        return this.hasFinished;
    }

    public abstract void useIncoming();
    public abstract void processIncoming(byte[] bytes);
    public abstract byte[] processOutgoing();
}
