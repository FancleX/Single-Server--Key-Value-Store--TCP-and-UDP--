package org.example.client;

import org.example.protocol.Message;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import java.util.Scanner;

/**
 * A client to start a tcp connection, mainly take care with user interaction and request sending.
 * Client will be timed out either loss connection with server or user does not perform action for a
 * while.
 */
public class UDPClient extends AbstractClient implements Runnable {

    // client socket
    private final DatagramSocket socket;

    // host port
    private final int hostPort;

    // host name
    private final InetAddress hostName;

    // response reading
    private final Client client;

    // packet buffer
    private final byte[] buffer;

    public UDPClient(String hostName, int port) throws UnknownHostException, SocketException {
        this.hostPort = port;
        this.hostName = InetAddress.getByName(hostName);
        // create a local socket
        socket = new DatagramSocket();
        //    socket = new DatagramSocket(4990);
        // time out if the client can not hear server response in 20 seconds or client does not
        // operations
        socket.setSoTimeout(20000);
        // buffer size
        buffer = new byte[1024];
        // create Client instance
        client = new Client(this);
        Thread thread = new Thread(client);
        thread.start();
    }

    @Override
    public void run() {
        // log client start
        ClientLogger.clientStartLog(this);
        // create a buffer packet
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        // pre-populate key value to the server
        Message[] prepopulateStorage = prepopulateStorage();
        // send pre-populate data to the server
        packet.setAddress(hostName);
        packet.setPort(hostPort);
        Arrays.stream(prepopulateStorage)
                .forEach(
                        item -> {
                            try {
                                packet.setData(Message.encoder(item));
                                socket.send(packet);
                                // add the packet to message queue
                                client.enqueue(item);
                            } catch (IOException e) {
                                // log exception
                                ClientLogger.exceptionLog(e);
                            }
                        });
        // ask for user to do the next step
        if (userInterface(packet) == 0) {
            // exit the client
            System.exit(0);
        }
    }

    /**
     * A user interface to interact with user input and handle events.
     *
     * @param packet packet
     * @return status code, 0 means user exit the client
     */
    public int userInterface(DatagramPacket packet) {
        // open a scanner for taking user input
        Scanner scanner = new Scanner(System.in);
        while (true) {
            ClientLogger.print(
                    "Please input a number to use services: 1. GET, 2. PUT, 3. DELETE, 4. Exit");
            String nextLine = scanner.nextLine();
            int option;
            try {
                option = Integer.parseInt(nextLine);
            } catch (NumberFormatException e) {
                // assign option a value to pass
                option = -1;
            }
            // client exits
            if (option == 4) {
                scanner.close();
                return 0;
            }
            // else handle other options
            Message result = optionHandler(option, scanner);
            // send the result to the server
            // reassign payload
            if (result != null) {
                try {
                    packet.setData(Message.encoder(result));
                    socket.send(packet);
                    // enqueue
                    client.enqueue(result);
                } catch (IOException e) {
                    // log exception
                    ClientLogger.exceptionLog(e);
                }
            }
        }
    }

    /**
     * Return socket of the client.
     *
     * @return socket
     */
    public DatagramSocket getSocket() {
        return socket;
    }
}
