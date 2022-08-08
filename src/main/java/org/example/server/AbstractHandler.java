package org.example.server;

import org.example.protocol.Message;
import org.example.protocol.MessageType;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Optional;

/**
 * Common handle methods for both tcp and udp handlers.
 */
public abstract class AbstractHandler {

    /**
     * Handle get request and return the value.
     *
     * @param key the key to get value
     * @return output message after handled
     */
    public Message getHandler(String key) {
        // retrieve data from storage
        Optional<String> value = KeyValue.getValueByKey(key);
        // if data is present, send the data back else send an error message
        if (value.isPresent()) {
            return Response.success(MessageType.GET, value.get());
        } else {
            return Response.fail(MessageType.GET);
        }
    }

    /**
     * Handle put request. Store or update the key value.
     *
     * @param key   the key to be inserted
     * @param value the value to be inserted or updated
     * @return output message after handled
     */
    public Message putHandler(String key, String value) {
        // store the key value pair
        KeyValue.put(key, value);
        // send response to client
        return Response.success(MessageType.PUT);
    }

    /**
     * Handle delete request. Delete key value if they are exited, else send an error.
     *
     * @param key the key to be deleted
     * @return output message after handled
     */
    public Message deleteHandler(String key) {
        // check if delete successfully
        if (KeyValue.delete(key)) {
            // send successful
            return Response.success(MessageType.DELETE);
        } else {
            // send error
            return Response.fail(MessageType.DELETE);
        }
    }

    /**
     * Analyze request from client and perform the actions, this method will print log of the received
     * message.
     *
     * @param request the request of the client
     * @return output message after handled
     */
    public Message requestAnalyzer(Message request, InetAddress address, int port, int length) {
        // get type of the request
        MessageType type = request.getType();
        HashMap<String, String> requestBody = new HashMap<>();
        switch (type) {
            // get request
            case GET:
                requestBody.put(request.getKey(), request.getValue());
                // print log
                ServerLogger.receiveLog(address, port, type, length, requestBody);
                return getHandler(request.getKey());
            // put request
            case PUT:
                requestBody.put(request.getKey(), request.getValue());
                // print log
                ServerLogger.receiveLog(address, port, type, length, requestBody);
                return putHandler(request.getKey(), request.getValue());
            // delete request
            case DELETE:
                requestBody.put(request.getKey(), request.getValue());
                // print log
                ServerLogger.receiveLog(address, port, type, length, requestBody);
                return deleteHandler(request.getKey());
            // other request type are not legal for client,
            // it will be processed as illegal request and report in log
            default:
                // print log
                ServerLogger.illegalRequestLog(address, port, length);
                return null;
        }
    }
}
