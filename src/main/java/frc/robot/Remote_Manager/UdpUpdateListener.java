package frc.robot.Remote_Manager;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import frc.robot.Constants;

public class UdpUpdateListener implements Runnable {

    private DatagramSocket sock;
    private DatagramPacket packet;
    private BiConsumer<String, Integer> onClientUpdate;
    private Supplier<String> getStateToSendToClient;

    /**
     * class to listen to and parse UDP datagrams from the client. spawns a seperate
     * threat that listens for packets, and once it receives one, it calls the
     * {@code updateMethod} consumer, then sends a reply containing a serialized version
     * of the return from the {@code getState} method
     * 
     * @param port         the port to open a socket on
     * @param updateMethod method called when the listener receives an update
     * @param getState     method called to get the current state, which is used as
     *                     a reply to the client
     */
    public UdpUpdateListener(String localIp, Integer port, BiConsumer<String, Integer> updateMethod,
            Supplier<String> getState) {
        try {
            // masking biggest 16 bits of int port so it is kept in UDP port range (why doesn't java have
            // unsigned number types??)
            if (port >= Short.MAX_VALUE - Short.MIN_VALUE) {
                System.out.println("[Remote Mechanism Manager] UDP port is outside legal range. defaulting to max value");
            }
            sock = new DatagramSocket(new InetSocketAddress(localIp, port & 0xFFFF));
            if (sock == null) {
                System.out.println("[Remote Mechanism Manager] unable to start UDP update listener server");
                return;
            }
            packet = new DatagramPacket(new byte[1024], 1024);
            onClientUpdate = updateMethod;
            getStateToSendToClient = getState;
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true) {
            packet = new DatagramPacket(new byte[1024], 1024);
            try {
                sock.receive(packet);
                byte[] data = packet.getData();
                if (data == new byte[]{}) {
                    System.out.println("[Remote Mechanism Manager] packet is blank");
                    return;
                }

                if (data[0] == 0) {
                    for (String arg : decodeUpdate(data)) {
                        String[] tidiedArg = arg.trim().split(":");

                        this.onClientUpdate.accept(
                            tidiedArg[0], Integer.parseInt(tidiedArg[1])
                        );
                    }
                }
                    
                byte[] prefix = {(byte)0};
                byte[] reply = this.getStateToSendToClient.get()
                    .getBytes(StandardCharsets.UTF_8);

                if (reply == new byte[]{(byte)0}) {
                    System.out.println("[Remote Mechanism Manager] reply is empty. did you register any subsystems?");
                    return;
                }
                
                byte[] combined = Arrays.copyOf(prefix, prefix.length + reply.length);
                System.arraycopy(reply, 0, combined, prefix.length, reply.length);
                
                DatagramPacket replyPacket = new DatagramPacket(new byte[1024], 1024);
                replyPacket.setSocketAddress(packet.getSocketAddress());
                replyPacket.setData(combined);
                
                sock.send(replyPacket);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static String[] decodeUpdate(byte[] b) {
        try {
            String decoded = new String(b, "UTF-8");
            return decoded.split(";");
        } catch (UnsupportedEncodingException e) {
            return new String[]{};
        }
    }

}