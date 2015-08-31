package eu.ondryaso.udp.client;

import eu.ondryaso.udp.packet.Packet;
import eu.ondryaso.udp.packet.PacketRegister;
import eu.ondryaso.udp.packet.PacketWriteStdout;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        PacketRegister.registerPacket(PacketWriteStdout.class, (short) 1);

        try {
            ClientNetcode client = new ClientNetcode("127.0.0.1", 1445, 1446);
            Packet p;
            int counter = 0;

            while(true) {
                client.sendPacketUdp(new PacketWriteStdout(null, "Packet #" + counter++));

                if((p = client.getProcessedPackets().poll()) != null) {
                    p.useIncoming();
                }

                Thread.sleep(50);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
