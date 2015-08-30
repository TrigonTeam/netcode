package eu.ondryaso.udp;

import eu.ondryaso.udp.packet.Packet;
import eu.ondryaso.udp.packet.PacketRegister;
import javafx.util.Pair;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Netcode {
    private Queue<Pair<Packet, byte[]>> toProcessIncoming = new ConcurrentLinkedQueue<>();
    private Queue<Packet> toProcessOutgoing = new ConcurrentLinkedQueue<>();
    private Queue<Packet> processed = new ConcurrentLinkedQueue<>();
    private int port;

    public Queue<Packet> getProcessedPackets() {
        return this.processed;
    }

    public void putPacketToProcess(Packet p) {
        this.toProcessOutgoing.add(p);
    }

    public Netcode(int port) {
        this.port = port;

        Thread i = new Thread(this::runIncoming);
        Thread o = new Thread(this::runOutgoing);
        Thread p = new Thread(this::process);

        i.start();
        o.start();
        p.start();
    }

    public void process() {
        while(!Thread.currentThread().isInterrupted()) {
            Pair<Packet, byte[]> proc = this.toProcessIncoming.poll();
            if(proc != null) {
                Packet p = proc.getKey();
                byte[] v = proc.getValue();
                byte[] a = new byte[v.length - 1];
                System.arraycopy(v, 1, a, 0, v.length - 1);

                p.processIncoming(a);
                this.processed.add(p);
            }
        }
    }

    public void runIncoming() {
        try {
            DatagramSocket socket = new DatagramSocket(this.port);

            while(!Thread.currentThread().isInterrupted()) {
                byte[] bytes = new byte[16384];
                DatagramPacket udpPacket = new DatagramPacket(bytes, bytes.length);
                socket.receive(udpPacket);

                Packet p = PacketRegister.createPacket(this, udpPacket.getAddress(), udpPacket.getPort(), bytes[0]);
                this.toProcessIncoming.add(new Pair<>(p, bytes));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void runOutgoing() {
        try {
            DatagramSocket socket = new DatagramSocket();

            while(!Thread.currentThread().isInterrupted()) {
                Packet p = this.toProcessOutgoing.poll();
                if(p != null) {
                    byte[] data = p.processOutgoing();
                    if(data != null) {
                        byte[] dataExt = new byte[16384];
                        dataExt[0] = PacketRegister.getPacketId(p.getClass());
                        System.arraycopy(data, 0, dataExt, 1, data.length % 16384);

                        DatagramPacket udpPacket = new DatagramPacket(dataExt, dataExt.length, p.getAddress(), p.getPort());
                        socket.send(udpPacket);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
