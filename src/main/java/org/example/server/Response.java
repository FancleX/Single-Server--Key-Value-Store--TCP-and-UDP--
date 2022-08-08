package org.example.server;

import org.example.protocol.Message;
import org.example.protocol.MessageType;

/**
 * Response class takes care of sending response back to the client.
 */
public class Response {

    /**
     * Generate a successful message once the request is done by server.
     *
     * @param requestType record the type of request for client to identify if is the right response
     * @return successful message
     */
    public static Message success(MessageType requestType) {
        Message success =
                new Message(MessageType.RESPONSE_NORMAL, requestType.toString(), "Request Completed");
        return success;
    }

    /**
     * Generate a successful message once the request is done by server for get request.
     *
     * @param requestType record the type of request for client to identify if is the right response
     * @param value       the value of get request
     * @return successful message
     */
    public static Message success(MessageType requestType, String value) {
        Message success = new Message(MessageType.RESPONSE_NORMAL, requestType.toString(), value);
        return success;
    }

    /**
     * Generate a failed message once the request is failed to be processed.
     *
     * @return failed message
     */
    public static Message fail(MessageType requestType) {
        Message fail =
                new Message(MessageType.RESPONSE_ERROR, requestType.toString(), "The key doesn't exist");
        return fail;
    }
}
