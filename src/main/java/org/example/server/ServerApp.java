package org.example.server;

import java.io.IOException;
import java.net.SocketException;

/**
 * A class to start two servers, TCP and UDP at different ports provided by command line arguments.
 */
public class ServerApp {

    public static void main(String[] args) {
        if (args.length == 2) {
            // take tcp port and udp port for the server
            String TCPPort = args[0];
            String UDPPort = args[1];
            // check validation
            try {
                int TCP = Integer.parseInt(TCPPort);
                int UDP = Integer.parseInt(UDPPort);
                // check if the port is in valid range
                if ((TCP >= 0 && TCP <= 65535) && (UDP >= 0 && UDP <= 65535)) {
                    // start two handlers
                    Thread tcp = new Thread(new TCPHandler(TCP));
                    Thread udp = new Thread(new UDPHandler(UDP));
                    tcp.start();
                    udp.start();
                } else {
                    ServerLogger.print("Invalid port, port range is from 0 to 65535");
                }
                // exception handle
            } catch (NumberFormatException e) {
                ServerLogger.print("Invalid arguments");
            } catch (SocketException e) {
                ServerLogger.print("Error when creating socket");
            } catch (IOException e) {
                ServerLogger.print("Error in IO connection");
            }
        } else {
            ServerLogger.print("Not enough arguments provided");
        }
    }
}
