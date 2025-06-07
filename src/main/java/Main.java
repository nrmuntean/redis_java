import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
  public static void main(String[] args) {
        ServerSocket serverSocket = null;
        Socket clientSocket = null;

        int port = 6379;

        try {
            serverSocket = new ServerSocket(port);
            serverSocket.setReuseAddress(true);

            System.out.println("Server is running and waiting for a connection on port " + port + "...");
            clientSocket = serverSocket.accept();

            InputStream inputStream = clientSocket.getInputStream();
            System.out.println("Connection accepted from " + clientSocket.getInetAddress() + ":" + clientSocket.getPort());

            // byte[] message = inputStream.readAllBytes();
            // String messageStr = new String(message);

            // System.out.println(messageStr);

            OutputStream outputStream = clientSocket.getOutputStream();
            outputStream.write("+PONG\r\n".getBytes());
            System.out.println("Sent response to client: +PONG");
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        } finally {
            try {
                if (clientSocket != null) {
                    clientSocket.close();
                    System.out.println("Client socket closed.");
                } 
            } catch (IOException e) {
                System.out.println("Error closing client socket: " + e.getMessage());
            }
        }
  }
}
