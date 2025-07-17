import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
  public static void main(String[] args) {
        ServerSocket serverSocket = null;
        int port = 6379;

        try {
            serverSocket = new ServerSocket(port);
            serverSocket.setReuseAddress(true);
            System.out.println("Server is running and waiting for a connection on port " + port + "...");

            // Event loop to handle multiple client connections
            while (true) {
                Socket clientSocket = null;
                try {
                    // Accept a client connection
                    clientSocket = serverSocket.accept();
                    System.out.println("Connection accepted from " + clientSocket.getInetAddress() + ":" + clientSocket.getPort());

                    // Handle client communication
                    handleClient(clientSocket);
                } catch (IOException e) {
                    System.out.println("IOException while handling client: " + e.getMessage());
                } finally {
                    // Ensure the client socket is closed
                    if (clientSocket != null) {
                        try {
                            clientSocket.close();
                            System.out.println("Client socket closed.");
                        } catch (IOException e) {
                            System.out.println("Error closing client socket: " + e.getMessage());
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        } finally {
            try {
                if (serverSocket != null) {
                    serverSocket.close();
                    System.out.println("Server socket closed.");
                } 
            } catch (IOException e) {
                System.out.println("Error closing server socket: " + e.getMessage());
            }
        }
  }

  private static void handleClient(Socket clientSocket) throws IOException {
    InputStream inputStream = clientSocket.getInputStream();
    OutputStream outputStream = clientSocket.getOutputStream();

    byte[] buffer = new byte[1024];
    int bytesRead;

    while ((bytesRead = inputStream.read(buffer)) != -1) {
        // Convert only the valid portion of the buffer to a string
        String message = new String(buffer, 0, bytesRead);
        if (
                !message.isEmpty() 
                && message.equals("*1\r\n$4\r\nPING\r\n")
            ) {
            System.out.println("Received message: " + message);
            outputStream.write("+PONG\r\n".getBytes());
            System.out.println("Sent response to client: +PONG");
            break;
        }
        else {
            System.out.println("Unexpected message received.");
            System.out.println("Received message: " + message);
            break;
        }
    }
  }
}
