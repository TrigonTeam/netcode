package eu.ondryaso.udp;

import eu.ondryaso.udp.packet.Packet;
import eu.ondryaso.udp.packet.PacketRegister;
import eu.ondryaso.udp.packet.PacketWriteStdout;


public class Main {
    public static void main(String[] args) {
        PacketRegister.registerPacket(PacketWriteStdout.class, (byte) 1);
        Netcode c = new Netcode(6247);

        try {
            while (true) {
                Packet p = c.getProcessedPackets().poll();

                if (p != null) {
                    p.useIncoming();

                    c.putPacketToProcess(new PacketWriteStdout(c, p.getAddress(), p.getPort(), "k ty"));
                }

                Thread.sleep(15);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }


}
