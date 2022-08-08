package org.example.protocol;

import java.io.*;

/**
 * A serialized object used for communication between the client and server.
 */
public class Message implements Serializable {

    private static final long serialVersionUID = 123L;

    // type of the message
    private MessageType type;
    // key
    private String key;
    // value
    private String value;

    public Message() {
    }

    public Message(MessageType type, String key, String value) {
        this.type = type;
        this.key = key;
        this.value = value;
    }

    /**
     * Encode a Message object to byte array.
     *
     * @param message Message object to be encoded
     * @return byte array of the message object
     */
    public static byte[] encoder(Message message) throws IOException {
        // open a byte output stream
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        // open an object output stream
        ObjectOutputStream out = new ObjectOutputStream(byteArrayOutputStream);
        // write the object in
        out.writeObject(message);
        // convert object to byte array
        byte[] response = byteArrayOutputStream.toByteArray();
        // close stream
        out.close();
        return response;
    }

    /**
     * Decode a byte array to Message object.
     *
     * @param packetData byte array to be decoded
     * @return Message object of the byte array
     */
    public static Message decoder(byte[] packetData) throws IOException, ClassNotFoundException {
        // open a byte input stream and read the packet info in
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(packetData);
        // open an object input stream
        ObjectInputStream in = new ObjectInputStream(byteArrayInputStream);
        // cover the byte array to the Message object
        Message request = (Message) in.readObject();
        // close stream
        in.close();
        return request;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
