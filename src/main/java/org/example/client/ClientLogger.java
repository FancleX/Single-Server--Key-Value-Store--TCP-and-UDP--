package org.example.client;

import org.example.protocol.Message;
import org.example.protocol.MessageType;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Map;

/**
 * Logger of client print every formatted log to the console.
 */
public class ClientLogger {

    /**
     * Log none responded request.
     *
     * @param message none responded message
     */
    public static void nonRespondedRequestLog(Message message) {
        String currentTime = getCurrentTime();
        System.out.println(
                currentTime
                        + " : "
                        + message.getType()
                        + " request with body {key: "
                        + message.getKey()
                        + " : value: "
                        + message.getValue()
                        + "} is not Responded by Server");
    }

    /**
     * Log the invalid response that not from server.
     *
     * @param address the ip address of the response
     * @param port    the port of the response
     * @param length  the length of the response
     */
    public static void invalidResponseReceivedLog(InetAddress address, int port, int length) {
        String currentTime = getCurrentTime();
        System.out.println(
                currentTime
                        + ": Received unsolicited response acknowledging unknown PUT/GET/DELETE with an invalid KEY of length "
                        + length
                        + " bytes from "
                        + address
                        + ": "
                        + port);
    }

    /**
     * Print log when receive an illegal response.
     *
     * @param responseType the response type of the server
     * @param requestType  the request type sent to the server
     * @param length       the length of the packet
     * @param body         the body of the response and original request, come with a key value pair
     */
    public static void illegalReceiveLog(
            MessageType responseType, MessageType requestType, int length, Map<String, String> body) {
        String currentTime = getCurrentTime();
        System.out.println(
                currentTime
                        + ": Received unexpected "
                        + responseType
                        + " response of length "
                        + length
                        + " bytes with body "
                        + "{Response type: "
                        + body.keySet().toArray()[0]
                        + " : value: "
                        + body.values().toArray()[0]
                        + "}, but the original request was "
                        + requestType
                        + "with body {key: "
                        + body.keySet().toArray()[1]
                        + " : value: "
                        + body.values().toArray()[1]
                        + " from Server");
    }

    /**
     * Print log when receive a legal response.
     *
     * @param responseType the response type of the server
     * @param length       the length of the packet
     * @param body         the body of the response, come with a key value pair
     */
    public static void legalReceiveLog(
            MessageType responseType, int length, Map<String, String> body) {
        String currentTime = getCurrentTime();
        System.out.println(
                currentTime
                        + ": Received "
                        + responseType
                        + " response of length "
                        + length
                        + " bytes with body "
                        + "{Request type: "
                        + body.keySet().toArray()[0]
                        + " : response: "
                        + body.values().toArray()[0]
                        + "}"
                        + " from Server");
    }

    /**
     * Log the exception during the client processing.
     *
     * @param e exception
     */
    public static void exceptionLog(Exception e) {
        String currentTime = getCurrentTime();
        if (e instanceof SocketTimeoutException) {
            System.out.println(
                    currentTime
                            + ": Server is not responded or client doesn't do any actions, client is timed out...");
        } else if (e instanceof IOException) {
            System.out.println(currentTime + ": Error in IO connection");
        } else if (e instanceof ClassNotFoundException) {
            System.out.println(currentTime + ": The Message Class not found");
        } else if (e instanceof NumberFormatException) {
            System.out.println(currentTime + ": Please input an valid integer");
        }
    }

    /**
     * Log when client started.
     *
     * @param client the started client
     */
    public static void clientStartLog(AbstractClient client) {
        String currentTime = getCurrentTime();
        if (client instanceof UDPClient) {
            System.out.println(currentTime + ": UDP Client started");
        } else {
            System.out.println(currentTime + ": TCP Client started");
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
