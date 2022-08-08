package org.example.client;

import org.example.protocol.Message;
import org.example.protocol.MessageType;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

/**
 * Common methods for both TCP and UDP clients.
 */
public abstract class AbstractClient {

    /**
     * Analyze the response with last request message to determine if it is an expected request.
     *
     * @param response    current received response
     * @param lastRequest last sent request
     * @return -1 means unknown response, but it is a message object with incorrect response type; 0
     * means correct response; 1 means the response didn't match the request; 2 means no response should be received this time
     */
    public static int responseHandler(
            Message response,
            Map<Message, Long> lastRequest,
            InetAddress senderAddress,
            int senderPort,
            int packetLength) {
        HashMap<String, String> map = new HashMap<>();
        // check message type
        try {
            Message last = (Message) lastRequest.keySet().toArray()[0];
            if (MessageType.RESPONSE_NORMAL.equals(response.getType())
                    || MessageType.RESPONSE_ERROR.equals(response.getType())) {
                // check message key from server which record which request is
                if (last.getType().toString().equals(response.getKey())) {
                    // now it is the expected response and print it in log
                    map.put(response.getKey(), response.getValue());
                    ClientLogger.legalReceiveLog(response.getType(), packetLength, map);
                    return 0;
                }
                // server gave an invalid response which is a different request type
                // log it
                map.put(response.getKey(), response.getValue());
                map.put(last.getKey(), last.getValue());
                ClientLogger.illegalReceiveLog(response.getType(), last.getType(), packetLength, map);
                return 1;
            }
            // it is an unknown response, but it is a message object with incorrect response type
            // mark it in log
            ClientLogger.invalidResponseReceivedLog(senderAddress, senderPort, packetLength);
            return -1;
        } catch (NullPointerException e) {
            // it is an unknown response, but it is a message object with incorrect response type
            // but currently not suppose to receive a response because no message in message queue
            // mark it in log
            ClientLogger.invalidResponseReceivedLog(senderAddress, senderPort, packetLength);
            return 2;
        }
    }

    /**
     * Handle the option that user chose.
     *
     * @param option  option
     * @param scanner current scanner
     * @return fabricated message object according to options
     */
    public Message optionHandler(int option, Scanner scanner) {
        Message message = null;
        String key;
        String value;
        String tip;
        switch (option) {
            // helper message of get request
            case 1:
                tip = "Input a key to establish the GET request (up to 80 characters not empty): ";
                // check validation
                key = checkValidation(tip, scanner);
                // do request
                message = getRequest(key);
                break;
            // helper message of put request
            case 2:
                tip = "Input a key to establish the PUT request (up to 80 characters not empty): ";
                key = checkValidation(tip, scanner);
                tip = "Then input a value to establish the request (up to 80 characters not empty): ";
                value = checkValidation(tip, scanner);
                // do request
                message = putRequest(key, value);
                break;
            // helper message of delete request
            case 3:
                tip = "Input a key to establish the DELETE request (up to 80 characters not empty): ";
                key = checkValidation(tip, scanner);
                // do request
                message = deleteRequest(key);
                break;
            // invalid option
            default:
                ClientLogger.print("Please choose an legal integer to take a request!");
        }
        // fabricate the message object
        return message;
    }

    /**
     * Check user input.
     *
     * @param scanner scanner
     * @return a valid string
     */
    public String checkValidation(String tip, Scanner scanner) {
        while (true) {
            ClientLogger.print(tip);
            String str = scanner.nextLine();
            // check the length
            if ((str.length() > 80) || (str.length() == 0)) {
                ClientLogger.print("Up to 80 characters and cannot be empty!");
            } else {
                return str;
            }
        }
    }

    /**
     * Generate a get request.
     *
     * @param key the key to get its value
     * @return a message object of get request
     */
    public Message getRequest(String key) {
        Message request = new Message(MessageType.GET, key, null);
        return request;
    }

    /**
     * Generate a put request.
     *
     * @param key   the key to store or update its value
     * @param value the value of the key
     * @return a message object of put request
     */
    public Message putRequest(String key, String value) {
        Message request = new Message(MessageType.PUT, key, value);
        return request;
    }

    /**
     * Generate a delete request.
     *
     * @param key the key to be deleted
     * @return a message object of delete request
     */
    public Message deleteRequest(String key) {
        Message request = new Message(MessageType.DELETE, key, null);
        return request;
    }

    /**
     * Generate key value pairs for pre-populating usage, it will generate five random key value
     * pairs.
     *
     * @return a message array contains pre-populated info
     */
    public Message[] prepopulateStorage() {
        // predefined key values
        String[] keys = {"abc", "asd", "qwe", "978", "cvc"};
        String[] values = {"2323", "dfasdf", "32idfsd", "fdsaf023", "fds3223"};
        // generate a random key value pairs
        Random random = new Random();
        // store data in an array
        Message[] pairs = new Message[5];
        for (int i = 0; i < 5; i++) {
            // randomly choose key values
            int index = random.nextInt(5);
            pairs[i] = new Message(MessageType.PUT, keys[index], values[index % 2]);
        }
        return pairs;
    }
}
