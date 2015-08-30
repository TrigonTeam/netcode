package eu.ondryaso.udp;

import eu.ondryaso.udp.packet.Packet;
import eu.ondryaso.udp.packet.PacketRegister;
import eu.ondryaso.udp.packet.PacketWriteStdout;

import java.net.InetAddress;
import java.net.Socket;

public class Main {
    public static void main(String[] args) {
        PacketRegister.registerPacket(PacketWriteStdout.class, (byte) 1);
        Netcode c = new Netcode(6247);

        try {
            Socket s = new Socket("8.8.8.8", 53);
            InetAddress a = InetAddress.getByName(s.getLocalAddress().getHostAddress());

            int count = 1;
            while (true) {
                c.putPacketToProcess(new PacketWriteStdout(c, a, 6247, "Packet #" + count++));
                Packet p = c.getProcessedPackets().poll();

                if (p != null)
                    p.useIncoming();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }


}
