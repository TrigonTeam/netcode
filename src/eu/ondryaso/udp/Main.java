package eu.ondryaso.udp;

import eu.ondryaso.udp.packet.PacketRegister;
import eu.ondryaso.udp.packet.PacketWriteStdout;

public class Main {
    public static void main(String[] args) {
        PacketRegister.registerPacket(PacketWriteStdout.class, (byte) 1);
        Netcode c = new Netcode();

        while (true) {

        }
    }


}
