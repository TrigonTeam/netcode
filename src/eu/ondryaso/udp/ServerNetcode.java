package eu.ondryaso.udp;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import eu.ondryaso.udp.packet.Packet;
import eu.ondryaso.udp.packet.PacketRegister;
import org.xerial.snappy.Snappy;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ServerNetcode extends Listener {

    private Queue<IncomingPacketBlob> toProcessIncoming = new ConcurrentLinkedQueue<>();
    private Queue<Packet> processedIncoming = new ConcurrentLinkedQueue<>();
    private Queue<OutgoingPacketBlob> toProcessOutgoing = new ConcurrentLinkedQueue<>();

    private Server server;

    public ServerNetcode(int tcp, int udp) throws IOException {
        Thread ip = new Thread(this::processIncoming);
        Thread op = new Thread(this::processOutgoing);

        this.server = new Server();
        this.server.start();
        this.server.getKryo().register(byte[].class);
        this.server.bind(tcp, udp);
        this.server.addListener(this);

        ip.start();
        op.start();
    }

    @Override
    public void connected(Connection connection) {
        System.out.println("Connected #" + connection.getID() + ":" +
                connection.getRemoteAddressTCP().getHostName() + "/" +
                connection.getRemoteAddressTCP().getPort());
    }

    @Override
    public void disconnected(Connection connection) {
        System.out.println("Disconnected #" + connection.getID());
    }

    public Queue<Packet> getProcessedPackets() {
        return this.processedIncoming;
    }

    public void sendPacketUdp(int clientId, Packet packet) {
        this.toProcessOutgoing.add(new OutgoingPacketBlob(clientId, packet, false));
    }

    public void sendPacketTcp(int clientId, Packet packet) {
        this.toProcessOutgoing.add(new OutgoingPacketBlob(clientId, packet, true));
    }

    private void processOutgoing() {
        OutgoingPacketBlob b;

        while (!Thread.currentThread().isInterrupted()) {
            if ((b = this.toProcessOutgoing.poll()) != null) {
                try {
                    short id = PacketRegister.getPacketId(b.packet.getClass());
                    int ids = id << 1;

                    byte[] bytes = b.packet.processOutgoing();
                    if(bytes.length >= 128) {
                        bytes = Snappy.compress(bytes);
                        ids |= 1;
                    }

                    byte[] withMeta = new byte[bytes.length + 2];
                    System.arraycopy(bytes, 0, withMeta, 2, bytes.length);

                    withMeta[0] = (byte)(ids & 0xFF);
                    withMeta[1] = (byte)((ids >> 8) & 0xFF);

                    if (b.tcp) {
                        this.server.sendToTCP(b.clientId, withMeta);
                    } else {
                        this.server.sendToUDP(b.clientId, withMeta);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void processIncoming() {
        IncomingPacketBlob b;

        while (!Thread.currentThread().isInterrupted()) {
            if ((b = this.toProcessIncoming.poll()) != null) {
                try {
                    byte[] data = b.rawData;

                    int ids = ((data[0] & 0xFF) | (data[1] << 8) & 0xFFFE);
                    short id = (short) (ids >> 1);

                    byte[] packetData = new byte[data.length - 2];
                    Packet p = PacketRegister.createPacket(b.connection, id);

                    if (p != null) {
                        System.arraycopy(data, 2, packetData, 0, packetData.length);

                        if((data[0] & 1) == 1) {
                            packetData = Snappy.uncompress(packetData);
                        }

                        p.processIncoming(packetData);
                        this.processedIncoming.add(p);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void received(Connection connection, Object o) {
        if (o instanceof byte[]) {
            byte[] dataCompressed = (byte[]) o;
            this.toProcessIncoming.add(new IncomingPacketBlob(connection, dataCompressed));
        }
    }

    private class IncomingPacketBlob {
        public Connection connection;
        public byte[] rawData;

        public IncomingPacketBlob(Connection connection, byte[] data) {
            this.connection = connection;
            this.rawData = data;
        }
    }

    private class OutgoingPacketBlob {
        public int clientId;
        public Packet packet;
        public boolean tcp;

        public OutgoingPacketBlob(int clientId, Packet packet, boolean useTcp) {
            this.clientId = clientId;
            this.packet = packet;
            this.tcp = useTcp;
        }
    }


}
