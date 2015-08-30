package eu.ondryaso.udp.packet;

import eu.ondryaso.udp.Netcode;

import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;

public class PacketRegister {
    private static Map<Byte, Class<? extends Packet>> packets = new HashMap<>();
    private static Map<Class<? extends Packet>, Byte> pids = new HashMap<>();

    public static void registerPacket(Class<? extends Packet> packet, byte id) {
        if(!PacketRegister.packets.containsKey(id)) {
            PacketRegister.packets.put(id, packet);
            PacketRegister.pids.put(packet, id);
        }
    }

    public static Class<? extends Packet> getPacket(byte id) {
        return PacketRegister.packets.get(id);
    }

    public static byte getPacketId(Class<? extends Packet> packet) {
        return PacketRegister.pids.get(packet);
    }

    public static Packet createPacket(Netcode server, InetAddress address, int port, byte id) {
        Class<? extends Packet> c = PacketRegister.getPacket(id);
        if(c != null) {
            try {
                return c.getConstructor(Netcode.class, InetAddress.class, int.class).newInstance(server, address, port);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        } else {
            return null;
        }
    }
}
