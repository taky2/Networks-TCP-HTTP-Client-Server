//package server;
/**
 * Creates a thread for each connection request from a client
 * in order to handle multiple client connections simultaneously.
 * 
 * ATTENTION:   RUN THIS FILE TO START SERVER.
 * 
 * Required files:  TCPMultiServerThread.java
 *                  TCPClient.java (running client side) 
 *                  CS3700.htm (must be in server's root directory to test HTTP GET request)
 *
 * @author Dustin Fay
 */
import java.net.*;
import java.io.*;

/**
 * TCPMultiServer opens a ServerSocket and listens for HTTP requests over port 5678
 * and passes the socket information forward to TCPMultiServerThread.java 
 */
public class TCPMultiServer {
    
    public static void main(String[] args) throws IOException {
        
	ServerSocket serverTCPSocket = null;
	boolean listening = true;
	try {
            serverTCPSocket = new ServerSocket(5678);
	}catch (IOException e) {
            System.err.println("Could not listen on port: 5678.");
            System.exit(-1);
	}
	while (listening) {
            new TCPMultiServerThread(serverTCPSocket.accept()).start();
	}
        serverTCPSocket.close();
    }//end main
}//end TCPMultiServer
