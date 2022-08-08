package org.example.client;

import org.example.protocol.Message;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Arrays;
import java.util.Scanner;

/**
 * A client to start a tcp connection, mainly take care with user interaction and request sending.
 * Client will be timed out either loss connection with server or user does not perform action for a
 * while.
 */
public class TCPClient extends AbstractClient implements Runnable {

    // client socket
    private final Socket socket;

    // response reading
    private final Client client;

    // output stream
    private ObjectOutputStream out;

    /**
     * Construct a tcp client.
     *
     * @param hostName host name
     * @param port     port
     * @throws IOException exception
     */
    public TCPClient(String hostName, int port) throws IOException {
        // create a socket and build connection
        socket = new Socket();
        SocketAddress socketAddress = new InetSocketAddress(hostName, port);
        // set time out
        socket.connect(socketAddress, 20000);
        // time out if the client can not hear server response in 20 seconds or client does not
        // operations
        socket.setSoTimeout(20000);
        // create Client instance
        client = new Client(this);
        Thread thread = new Thread(client);
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public void run() {
        // log client start
        ClientLogger.clientStartLog(this);
        // create output stream
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            // pre-populate key value to the server
            Message[] prepopulateStorage = prepopulateStorage();
            // send pre-populate data to the server
            Arrays.stream(prepopulateStorage)
                    .forEach(
                            item -> {
                                try {
                                    this.out.writeObject(item);
                                    // push into the queue
                                    client.enqueue(item);
                                } catch (IOException e) {
                                    ClientLogger.exceptionLog(e);
                                }
                            });
            // ask for user to do the next step
            if (userInterface(this.out) == 0) {
                // exit the client
                System.exit(0);
            }
        } catch (IOException e) {
            ClientLogger.exceptionLog(e);
        }
    }

    /**
     * A user interface to interact with user input and handle events.
     *
     * @param out output stream
     * @return status code, 0 means user exit the client
     */
    public int userInterface(ObjectOutputStream out) {
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
                    out.writeObject(result);
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
    public Socket getSocket() {
        return socket;
    }
}
