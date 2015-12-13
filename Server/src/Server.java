import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Server {

    public static void main(String[] args) throws IOException {
        new Server().startServer();

        String centralServerHostName = new String("172.17.78.101");

        // Server to ping
        String serverHostname = new String ("172.17.78.101");

        if (args.length > 0)
           serverHostname = args[0];
       // System.out.println ("Attemping to connect to host " + serverHostname + " on port 8000.");

        Socket echoSocket = null;
        long startTime, endTime;
        try {
            System.out.println("Client: Connecting to " + serverHostname + " on port " + 8000);
            echoSocket = new Socket(serverHostname, 8000);
            System.out.println("Client: Just connected to " + echoSocket.getRemoteSocketAddress());
            
            
            OutputStream outToServer = echoSocket.getOutputStream();
            DataOutputStream out = new DataOutputStream(outToServer);
            
            // Connect to central server.
            Socket centralServer = new Socket(centralServerHostName, 8000);
            OutputStream outToCentralServer = centralServer.getOutputStream();
            DataOutputStream outCS = new DataOutputStream(outToCentralServer);
            
            // Record time sent.
            while(true) {
              startTime = TimeUnit.NANOSECONDS.toMicros(System.nanoTime());
              out.writeUTF("Hello");
              
              
              InputStream inFromServer = echoSocket.getInputStream();
              DataInputStream in = new DataInputStream(inFromServer);
              
              // Record time received.
              endTime = TimeUnit.NANOSECONDS.toMicros(System.nanoTime());
              
              //System.out.println("Client: Server says " + in.readUTF());
              System.out.println("Client: Received msg from server");
              System.out.println("Client: Time it took = " + (endTime-startTime));
              
              // Send data to central server.
              outCS.writeUTF(Long.toString(endTime-startTime));
              try {
                Thread.sleep(1000);
              } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
              }  
              
            }
                    
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host: " + serverHostname);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for " + "the connection to: " + serverHostname + "\n" + e);
            System.exit(1);
        }

        echoSocket.close();
    } // End main

    public void startServer() {
        final ExecutorService clientProcessingPool = Executors.newFixedThreadPool(10);

        Runnable serverTask = new Runnable() {
            @Override
            public void run() {
                try {
                    ServerSocket serverSocket = new ServerSocket(8000);
                    System.out.println("Server: Waiting for clients to connect...");
                    while (true) {
                        Socket clientSocket = serverSocket.accept();
                        clientProcessingPool.submit(new ClientTask(clientSocket));
                    }
                } catch (IOException e) {
                    System.err.println("Unable to process client request");
                    e.printStackTrace();
                }
            }
        };
        Thread serverThread = new Thread(serverTask);
        serverThread.start();

    }

    private class ClientTask implements Runnable {
        private final Socket clientSocket;

        private ClientTask(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            System.out.println("Server: Got a client from: " + clientSocket.getInetAddress().getHostAddress());

            try {
//              System.out.println("Just connected to " + clientSocket.getRemoteSocketAddress());
              DataInputStream in = new DataInputStream(clientSocket.getInputStream());
              //System.out.println(in.readUTF());
              while (in.readUTF() != null) {
                DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
                out.writeUTF("Thank you for connecting to " + clientSocket.getLocalSocketAddress());
              }
              clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}