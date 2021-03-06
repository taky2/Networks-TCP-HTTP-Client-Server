/**
 * Creates a thread for each connection request from a client
 * in order to handle multiple client connections simultaneously.
 * 
 * ATTENTION: RUN THIS FILE TO START SERVER
 * 
 * Required files:  TCPMultiServerThread.java
 *                  TCPClient.java (running client side) 
 *                  CS3700.htm (to test HTTP GET request)
 * 
 * @author Dustin Fay
 */
 
import java.io.*;
import java.net.*;
import java.sql.Timestamp;

/**
 * TCPClient contains all core logic to initiate client connection to 
 * server and issue HTTP GET requests as specified in Homework 3. 
 */
public class TCPClient {
    /*
     * The start and stop times to calculate elapsed time from making a request
     * to receiving a response.
     */
    /** For response time of requests. */
    private Timestamp initReqTime, initRespTime, msgReqTime, msgRespTime;

    /** The rtt. */
    private long initRTT, msgRTT;

    /** The user agent. */
    private String hostname, methodType, htmFile, httpVersion, userAgent;

    /**
     * The main method starts the constructor.
     * 
     * @param args
     *            the arguments
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public static void main(String[] args) throws IOException {
        new TCPClient();
    }

    /**
     * Instantiates a new TCP client.
     * 
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public TCPClient() throws IOException {
        Socket tcpSocket = null;
        PrintWriter socketOut = null;
        BufferedReader socketIn = null;
        System.out.println("");
        System.out.println("Please input the DNS/IP of your HTTP server");
        BufferedReader sysIn = new BufferedReader(new InputStreamReader(System.in));
        hostname = sysIn.readLine();
        boolean connected = true;
        try { //calculate RTT for initial connection
            initReqTime = new Timestamp(System.currentTimeMillis());
            tcpSocket = new Socket(hostname, 5678);
            initRespTime = new Timestamp(System.currentTimeMillis());
            initRTT = initRespTime.getTime() - initReqTime.getTime();
            System.out.println("\r\nInitial Connection RTT: " + initRTT + " msec (time to establish connection)\r\n");
            socketOut = new PrintWriter(tcpSocket.getOutputStream(), true);
            socketIn = new BufferedReader(new InputStreamReader(tcpSocket.getInputStream()));
        } 
        catch (UnknownHostException e) {
            System.err.println("Error:" + hostname + " not valid.");
            System.exit(1);
        } 
        catch (IOException e) {
            System.err.println("Could not establish connection to: "+ hostname);
            System.exit(1);
        }
        while (connected) {
            System.out.println("Enter HTTP method type");
            methodType = sysIn.readLine().toUpperCase();
            System.out.println("Enter name of the file requested (EX: CS3700.htm)");
            htmFile = sysIn.readLine();
            System.out.println("Enter HTTP Version");
            httpVersion = sysIn.readLine().toUpperCase();
            System.out.println("Enter User-Agent");
            userAgent = sysIn.readLine();
            // HANDLE FORMATING BELOW
            if (htmFile.startsWith("/")) { // check prefix
            } else { htmFile = '/' + htmFile;} // add prefix
            if (httpVersion.startsWith("HTTP/")) { // check prefix
            } else {httpVersion = "HTTP/" + httpVersion;} // add prefix
            String requestLine = methodType.toUpperCase() + " " + htmFile + " " + httpVersion;
            String requestMessage = requestLine + "\r\n" + "Host: " + hostname
                                    + "\r\n" + "User-Agent: " + userAgent + "\r\n";
            System.out.println("Request Message: " + requestMessage);

            msgReqTime = new Timestamp(System.currentTimeMillis());
            socketOut.println(requestMessage);
            //check input from socket 
            int emptyLineCount = 0;
            String fromServer;
            while ((fromServer = socketIn.readLine()) != null) {
                if (!fromServer.isEmpty()) {
                    emptyLineCount = 0;
                    System.out.println(fromServer);
                    if (fromServer.contains("400 Bad Request") || fromServer.contains("404 Not Found")){
                        System.out.println(socketIn.readLine() + socketIn.readLine());
                        break;
                    } else if (fromServer.startsWith("null")) {
                        String file = htmFile.substring(1);
                        PrintWriter writer = new PrintWriter(file, "UTF-8");
                        writer.println(fromServer.substring(4));
                        writer.close();
                    } 
                } else {
                    emptyLineCount++;
                    // check for four empty lines => end of document.
                    if (emptyLineCount == 4)
                        break;
                }
            }//end while(!null)
            //calculate RTT for message 
            msgRespTime = new Timestamp(System.currentTimeMillis());
            msgRTT = msgRespTime.getTime() - msgReqTime.getTime();
            System.out.println();
            System.out.println("Finished.");
            System.out.println("File Transfer RTT: " + msgRTT + " msec (file transmission time)\n");
            String cont = null;
            //final prompt to user. option to close connection.
            do {
                System.out.println("Would you like to continue?");
                cont = sysIn.readLine().toUpperCase();
                if (cont.equals("NO") || cont.equals("N") || cont.equals("BYE")){
                    connected = false;
                    break;
                } else if (cont.equals("YES") || cont.equals("Y")) {
                    break;
                } else {
                    System.out.println("Invalid input, would you like to continue (yes or no)?");
                }
            } while (!(cont.equals("NO") || cont.equals("N") || cont.equals("BYE") || cont.equals("YES") || cont.equals("Y")));
            socketOut.println(cont);
        }//end while(connected)
        socketOut.close();
        socketIn.close();
        sysIn.close();
        tcpSocket.close();
        System.out.println("Connection closed.\n");
    }//end public TCPClient (inner)
}//end public class TCPClient (outer)
