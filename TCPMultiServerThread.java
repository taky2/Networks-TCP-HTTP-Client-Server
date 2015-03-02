/**
 * Homework 3 - CS3700.
 * @author Dustin Fay
 * 
 * Contains logic for multi-thread server side HTTP functionality 
 * 
 * Required files:  TCPMultiServer.java
 *                  TCPClient.java (running client side) 
 *                  CS3700.htm (to test HTTP GET request)
 */
import java.net.*;
import java.io.*;
import java.text.*;
import java.util.Date;

/**
 * TCPMultiServerThread handles all core logic for program TCPMultiServer.java.
 * Initiates a socket for use by client and issues responses to HTTP GET requests
 * from client as specified in Homework 3.
 */
public class TCPMultiServerThread extends Thread {
    private Socket clientTCPSocket = null;
    private PrintWriter clientSocketOut;
    private BufferedReader clientSocketIn;
    /**
     * Instantiate a new TCP multi server thread.
     */
    public TCPMultiServerThread(Socket socket) {
    	super("TCPMultiServerThread");
    	clientTCPSocket = socket;
    }
    /**
     * run() contains the bulk of the server-side logic enabling 
     * HTTP/GET functionality and client communication according
     * to specifications in Homework 3.
     */
    public void run() {
       	try {
            System.out.println("New request from client initiated.\n");
            do {
                clientSocketOut = new PrintWriter(clientTCPSocket.getOutputStream(), true);
                clientSocketIn = new BufferedReader(new InputStreamReader(clientTCPSocket.getInputStream()));
                String fromClient, toClient;
                String requestStatus = null;
                String fileBody = null;
                String statusLine = null;
                int lines = 0;
                int emptyLines = 0;
                //continue while new line is not null
                while ((fromClient = clientSocketIn.readLine()) != null) {
                    if (!fromClient.isEmpty()) {
                        emptyLines = 0;
                        lines++;
                        System.out.println(fromClient);
                        if (lines == 1) {
                            String[] clientArrayData = fromClient.split(" ");
                            if (clientArrayData[0].equals("GET")) {
                                fileBody = readFile(clientArrayData[1].substring(1));
                                if (fileBody.equals("error"))
                                    requestStatus = "404 Not Found";
                                else
                                    requestStatus = "200 OK";
                            } else {
                                requestStatus = "400 Bad Request";
                            }
                            statusLine = clientArrayData[2] + " " + requestStatus + "\r\n";
                        }
                    }//end outermost if statement -> start else... 
                    else {
                        emptyLines++;
                        //if line is empty -> reached end of header -> break.
                        if (emptyLines == 1)
                        break;
                    }   
                }//end while           
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                System.out.println();
                toClient = statusLine + "Date: " + dateFormat.format(date) + "\r\n\r\n" + "Server: AWS EC2 - Northern Virginia\r\n";
                if (requestStatus.equals("200 OK"))
                    toClient += fileBody + "\r\n\r\n\r\n\r\n";
                System.out.println("Response Sent. Press control+c to close at any time.\n");
                clientSocketOut.println(toClient);
            } //end do - start while (to check for no from client)
            while (!(clientSocketIn.readLine().equals("NO")));
            //END OF DO-WHILE -> otherwise close connection
            clientSocketOut.close();
            clientSocketIn.close();
            clientTCPSocket.close();
        } //end outermost try statement -> start catch block...
        catch (IOException e) {e.printStackTrace();}
    }//end run()

    /**
     * readFile takes in the file and reads it line-by-line until reader input is null.
     */
    public String readFile(String file) {
        FileReader fReader;
        try {
            fReader = new FileReader(file);
        } 
        catch (FileNotFoundException e) {
            e.printStackTrace();
            return "error";
        }
        BufferedReader textReader = new BufferedReader(fReader);
        String line = null;
        String allLines = "\r\n\r\n";
        try {
            while ((line = textReader.readLine()) != null) {
                allLines += line;
            }
            textReader.close();
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
        return allLines;
    }
}
