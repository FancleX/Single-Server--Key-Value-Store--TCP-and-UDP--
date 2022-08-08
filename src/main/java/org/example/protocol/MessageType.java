package org.example.protocol;

/**
 * Define message type of request and response
 */
public enum MessageType {
    // get, put, delete for client to send requests
    GET,
    PUT,
    DELETE,
    // normal and error for server to send responses
    // error means the client sent an invalid request, such as delete a non-existed key
    RESPONSE_NORMAL,
    RESPONSE_ERROR
}
