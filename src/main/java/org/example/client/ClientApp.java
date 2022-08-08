package org.example.client;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * A class to start a client, it can be TCP or UDP based on the command line arguments.
 */
public class ClientApp {

    public static void main(String[] args) {
        if (args.length == 3) {
            // take host name, port, protocol
            String hostName = args[0];
            String hostPort = args[1];
            String protocol = args[2];
            // check validation
            try {
                int port = Integer.parseInt(hostPort);
                // check port valid
                if ((port >= 0 && port <= 65535)) {
                    // check protocol
                    if ("TCP".equalsIgnoreCase(protocol)) {
                        // run tcp client
                        Thread thread = new Thread(new TCPClient(hostName, port));
                        thread.start();
                    } else if ("UDP".equalsIgnoreCase(protocol)) {
                        // run udp client
                        Thread thread = new Thread(new UDPClient(hostName, port));
                        thread.start();
                    } else {
                        // log invalid protocol
                        ClientLogger.print("Invalid protocol, please choose TCP or UDP");
                    }
                } else {
                    // log invalid port
                    ClientLogger.print("Invalid port, port range is from 0 to 65535");
                }
            } catch (NumberFormatException e) {
                // log exceptions
                ClientLogger.print("Invalid arguments");
            } catch (SocketException e) {
                ClientLogger.print("Error in creating socket or host refused connection");
            } catch (UnknownHostException e) {
                ClientLogger.print("Unknown host name or the host not found");
            } catch (IOException e) {
                ClientLogger.exceptionLog(e);
            }
        } else {
            // log not enough arguments
            ClientLogger.print("Not enough arguments provided");
        }
    }
}
