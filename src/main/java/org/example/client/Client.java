package org.example.client;

import org.example.protocol.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;

/**
 * A sub thread of TCP or UDP client, running only to listen to server response.
 */
public class Client extends AbstractClient implements Runnable {

    // determine if current running tcp or udp client
    private final AbstractClient client;

    // a message queue store sending request, will compare with response to identify the response
    // validation and check expired request
    private final MessageQueue messageQueue;

    /**
     * Construct a client to listen to any response of the socket and handle.
     *
     * @param client current running client
     */
    public Client(AbstractClient client) {
        this.client = client;
        messageQueue = new MessageQueue();
        Thread thread = new Thread(messageQueue);
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public void run() {
        // get socket
        // for udp connection
        if (client instanceof UDPClient) {
            DatagramSocket socket = ((UDPClient) client).getSocket();
            byte[] buffer = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            // start listening
            try {
                while (true) {
                    socket.receive(packet);
                    // check if is a malformed response or incorrect response
                    int result;
                    try {
                        // if from unknown source, the data cannot be decoded to Message object
                        Message response = Message.decoder(packet.getData());
                        // analyze the response message
                        result =
                                responseHandler(
                                        response,
                                        messageQueue.peek(),
                                        packet.getAddress(),
                                        packet.getPort(),
                                        packet.getLength());
                    } catch (IOException e) {
                        result = -1;
                        ClientLogger.invalidResponseReceivedLog(
                                packet.getAddress(), packet.getPort(), packet.getLength());
                    }
                    // if the response correct, remove it from message queue otherwise keep it until expired
                    if (result == 0) {
                        messageQueue.poll();
                    }
                }
            } catch (SocketTimeoutException e) {
                // catch socket time out
                ClientLogger.exceptionLog(e);
                System.exit(0);
            } catch (IOException | ClassNotFoundException e) {
                ClientLogger.exceptionLog(e);
            }
        } else {
            // for tcp connection
            Socket socket = ((TCPClient) client).getSocket();
            try {
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                while (true) {
                    // reading from socket
                    Message response = (Message) in.readObject();
                    // analyze the response message
                    int result =
                            responseHandler(
                                    response,
                                    messageQueue.peek(),
                                    socket.getInetAddress(),
                                    socket.getPort(),
                                    Message.encoder(response).length);
                    // if the response correct, remove it from message queue otherwise keep it until expired
                    if (result == 0) {
                        messageQueue.poll();
                    }
                }
            } catch (SocketTimeoutException e) {
                // catch socket time out
                ClientLogger.exceptionLog(e);
                System.exit(0);
            } catch (IOException | ClassNotFoundException e) {
                ClientLogger.print("Lost connection with the server");
                System.exit(0);
            }
        }
    }

    /**
     * Add a message object to the message queue.
     *
     * @param message message object
     */
    public void enqueue(Message message) {
        Map<Message, Long> object = new HashMap<>();
        object.put(message, System.currentTimeMillis());
        messageQueue.enqueue(object);
    }
}
