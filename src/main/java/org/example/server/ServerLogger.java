package org.example.server;

import org.example.protocol.MessageType;

import java.io.IOException;
import java.net.InetAddress;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Map;

/**
 * A logger class to print the log of the server. For legal request: Time: Received "Request Type"
 * request of length "length" with body {key : value} from "address" : "port" ... Time: Sent
 * "Response Type" of length "length" with body {request type : response content} to "address" :
 * "port" ... For illegal request: Time: Received malformed request of length "length" from
 * "address" : "port" ...
 */
public class ServerLogger {

    /**
     * Print log when receive a legal request.
     *
     * @param address     the ip address of the client
     * @param port        the port of the client
     * @param requestType the request type of the client
     * @param length      the length of the packet
     * @param body        the body of the request, come with a key value pair
     */
    public static void receiveLog(
            InetAddress address,
            int port,
            MessageType requestType,
            int length,
            Map<String, String> body) {
        String currentTime = getCurrentTime();
        System.out.println(
                currentTime
                        + ": Received "
                        + requestType
                        + " request of length "
                        + length
                        + " bytes with body "
                        + "{key: "
                        + body.keySet().toArray()[0]
                        + " : value: "
                        + body.values().toArray()[0]
                        + "}"
                        + " from "
                        + address.toString()
                        + ": "
                        + port);
    }

    /**
     * Log of server sends response.
     *
     * @param address      ip address of the destination
     * @param port         port of the destination
     * @param responseType the response type
     * @param length       the length of the packet
     * @param body         the body of the response, comes with key value pairs
     */
    public static void responseLog(
            InetAddress address,
            int port,
            MessageType responseType,
            int length,
            Map<String, String> body) {
        String currentTime = getCurrentTime();
        System.out.println(
                currentTime
                        + ": Sent "
                        + responseType
                        + " response of length "
                        + length
                        + " bytes with body "
                        + "{request type: "
                        + body.keySet().toArray()[0]
                        + " : response content: "
                        + body.values().toArray()[0]
                        + "} to "
                        + address
                        + ": "
                        + port);
    }

    /**
     * Log the illegal request.
     *
     * @param address the ip address of the request
     * @param port    the port of the request
     * @param length  the length of the packet
     */
    public static void illegalRequestLog(InetAddress address, int port, int length) {
        String currentTime = getCurrentTime();
        System.out.println(
                currentTime
                        + ": Received malformed request of length "
                        + length
                        + " bytes from "
                        + address
                        + ": "
                        + port);
    }

    /**
     * Log the exception during the server processing.
     *
     * @param e exception
     */
    public static void exceptionLog(Exception e) {
        String currentTime = getCurrentTime();
        if (e instanceof IOException) {
            System.out.println(currentTime + ": Error in IO connection");
        } else if (e instanceof ClassNotFoundException) {
            System.out.println(currentTime + ": The Message Class not found");
        }
    }

    /**
     * Log when server started.
     *
     * @param server the started server
     * @param port   the port of the server
     */
    public static void serverStartLog(AbstractHandler server, int port) {
        String currentTime = getCurrentTime();
        if (server instanceof UDPHandler) {
            System.out.println(currentTime + ": UDP is listening at port " + port);
        } else {
            System.out.println(currentTime + ": TCP is listening at port " + port);
        }
    }

    /**
     * Log for customized use.
     *
     * @param message message to be logged
     */
    public static void print(String message) {
        String currentTime = getCurrentTime();
        System.out.println(currentTime + ": " + message);
    }

    /**
     * Get current system time and format it.
     *
     * @return a formatted time
     */
    private static String getCurrentTime() {
        long currentTimeMillis = System.currentTimeMillis();
        Date date = new Date(currentTimeMillis);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        return dateFormat.format(date);
    }
}
