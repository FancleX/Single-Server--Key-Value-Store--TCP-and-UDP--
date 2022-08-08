package org.example.server;

import org.example.protocol.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

/**
 * Handle TCP request and give response.
 */
public class TCPHandler extends AbstractHandler implements Runnable {

    // tcp socket
    private final ServerSocket serverSocket;

    // IO stream
    private ObjectInputStream in;
    private ObjectOutputStream out;


    /**
     * Construct a TCP server and start listening.
     *
     * @param port the port of the server
     * @throws IOException io exception
     */
    public TCPHandler(int port) throws IOException {
        serverSocket = new ServerSocket(port);
    }

    @Override
    public void run() {
        // print the log when server started
        ServerLogger.serverStartLog(this, serverSocket.getLocalPort());
        while (true) {
            try {
                // read from connection
                Socket socket = serverSocket.accept();
                // create IO stream
                in = new ObjectInputStream(socket.getInputStream());
                out = new ObjectOutputStream(socket.getOutputStream());
                while (true) {
                    Message request = (Message) in.readObject();
                    // analyze request
                    Message response =
                            requestAnalyzer(
                                    request,
                                    socket.getInetAddress(),
                                    socket.getPort(),
                                    Message.encoder(request).length);
                    if (response != null) {
                        // send response
                        this.out.writeObject(response);
                        // print log
                        HashMap<String, String> responseBody = new HashMap<>();
                        responseBody.put(response.getKey(), response.getValue());
                        ServerLogger.responseLog(
                                socket.getInetAddress(),
                                socket.getPort(),
                                response.getType(),
                                Message.encoder(response).length,
                                responseBody);
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                // handle exceptions
                ServerLogger.print("Client lost connection");
                try {
                    in.close();
                    out.close();
                } catch (IOException ex) {
                    ServerLogger.exceptionLog(e);
                }
            }
        }
    }
}
