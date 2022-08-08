package org.example.test;

import org.example.protocol.Message;
import org.example.protocol.MessageType;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * A simple test for both UDP client and server. TCP follows the same logic.
 */
public class UDPTest {

    public static void main(String[] args) throws IOException {
        DatagramSocket socket = new DatagramSocket();
        byte[] buffer = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        // test server
        packet.setAddress(InetAddress.getLocalHost());
        // change to current server port
        packet.setPort(9002);
        // Test sender sent illegal message to server.
        packet.setData(new byte[]{13, 23, 123});
        socket.send(packet);
        // Test sender sent legal message but invalid request type
        packet.setData(Message.encoder(new Message(MessageType.RESPONSE_NORMAL, "dsf", "232")));
        socket.send(packet);

        // test client
        // change to current client port
        packet.setPort(62851);
        // Test sender sent illegal message to server.
        packet.setData(new byte[]{13, 23, 123});
        socket.send(packet);
        // Test sender sent legal message but invalid request type
        packet.setData(Message.encoder(new Message(MessageType.GET, "dsf", "232")));
        socket.send(packet);
    }
}
