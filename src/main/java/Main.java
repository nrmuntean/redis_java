import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
  public static void main(String[] args) {
        int port = 6379;
        System.out.println("Logs from your program will appear here!");

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            serverSocket.setReuseAddress(true);
            try (Socket clientSocket = serverSocket.accept()) {
                System.out.println("Server is running and has accepted a connection on port " + port + "!");

                InputStream inputStream = clientSocket.getInputStream();
                OutputStream outputStream = clientSocket.getOutputStream();

                byte[] message = inputStream.readAllBytes();
                String messageStr = new String(message);
                
                System.out.println(messageStr);

                System.out.println("Received EOF from client.");
                outputStream.write("+PONG\r\n".getBytes());
                clientSocket.close();
            } catch (IOException e) {
                System.out.println("IOException: " + e.getMessage());
            }
            
            serverSocket.close();
      } catch (IOException e) {
          System.out.println("IOException: " + e.getMessage());
      }
  }
}
