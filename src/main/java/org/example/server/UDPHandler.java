package org.example.server;

import org.example.protocol.Message;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashMap;

/**
 * Handle UDP request and give response.
 */
public class UDPHandler extends AbstractHandler implements Runnable {

    // udp socket
    private final DatagramSocket socket;

    // packet buffer
    private byte[] buffer;

    /**
     * Construct a UDP socket and start for listening at the port.
     *
     * @param port port to be listening
     */
    public UDPHandler(int port) throws SocketException {
        socket = new DatagramSocket(port);
        buffer = new byte[1024];
    }

    @Override
    public void run() {
        // print the log when server started
        ServerLogger.serverStartLog(this, socket.getLocalPort());
        while (true) {
            // create a buffer packet
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            try {
                // read from socket
                socket.receive(packet);
                // get client ip
                InetAddress clientAddress = packet.getAddress();
                // get client port
                int clientPort = packet.getPort();
                Message result;
                try {
                    // get data and decode, if from unknown resource it cannot be decoded to Message object
                    Message clientRequest = Message.decoder(packet.getData());
                    // analyze and handle request
                    result = requestAnalyzer(clientRequest, clientAddress, clientPort, packet.getLength());
                } catch (IOException e) {
                    ServerLogger.illegalRequestLog(clientAddress, clientPort, packet.getLength());
                    result = null;
                }
                if (result != null) {
                    // send the message back to the client
                    // encode message object to byte array
                    buffer = Message.encoder(result);
                    packet.setAddress(clientAddress);
                    packet.setPort(clientPort);
                    // add payload
                    packet.setData(buffer);
                    // send to the client
                    socket.send(packet);
                    // print log
                    HashMap<String, String> responseBody = new HashMap<>();
                    responseBody.put(result.getKey(), result.getValue());
                    ServerLogger.responseLog(
                            clientAddress, clientPort, result.getType(), buffer.length, responseBody);
                }
            } catch (IOException | ClassNotFoundException e) {
                // handle exceptions
                ServerLogger.exceptionLog(e);
                socket.close();
            }
        }
    }
}
