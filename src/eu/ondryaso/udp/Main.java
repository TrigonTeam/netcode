package eu.ondryaso.udp;


import eu.ondryaso.udp.packet.Packet;
import eu.ondryaso.udp.packet.PacketRegister;
import eu.ondryaso.udp.packet.PacketWriteStdout;

import java.io.IOException;


public class Main {
    public static void main(String[] args) {
        PacketRegister.registerPacket(PacketWriteStdout.class, (short) 1);

        try {
            ServerNetcode server = new ServerNetcode(1445, 1446);
            Packet p;

            while(true) {
                if((p = server.getProcessedPackets().poll()) != null) {
                    p.useIncoming();

                    server.sendPacketUdp(p.getConnection().getID(),
                            new PacketWriteStdout(p.getConnection(), "k ty"));
                }

                Thread.sleep(50);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }


}
