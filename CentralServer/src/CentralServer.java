import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CentralServer {

    public static void main(String[] args) {
        new CentralServer().startServer();
    }

    public void startServer() {
        final ExecutorService clientProcessingPool = Executors.newFixedThreadPool(10);

        Runnable serverTask = new Runnable() {
            @Override
            public void run() {
                try {
                    ServerSocket serverSocket = new ServerSocket(8000);
                    System.out.println("Waiting for clients to connect...");
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