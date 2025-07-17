import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.net.InetSocketAddress;
import java.util.Iterator;

public class Main {
    public static void main(String[] args) {
        int port = 6379;

        try (Selector selector = Selector.open();
             ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {

            // Configure the server socket channel
            serverSocketChannel.bind(new InetSocketAddress(port));
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            System.out.println("Server is running and waiting for connections on port " + port + "...");

            // Event loop to handle multiple client connections
            while (true) {
                selector.select(); // Block until at least one channel is ready
                Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();

                while (keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();
                    keyIterator.remove(); // Remove the key to avoid processing it again

                    if (key.isAcceptable()) {
                        // Accept a new client connection
                        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
                        SocketChannel clientChannel = serverChannel.accept();
                        clientChannel.configureBlocking(false);
                        clientChannel.register(selector, SelectionKey.OP_READ);
                        System.out.println("Connection accepted from " + clientChannel.getRemoteAddress());
                    } else if (key.isReadable()) {
                        // Handle client communication
                        SocketChannel clientChannel = (SocketChannel) key.channel();
                        handleClient(clientChannel, key);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }

    private static void handleClient(SocketChannel clientChannel, SelectionKey key) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        int bytesRead = clientChannel.read(buffer);

        if (bytesRead == -1) {
            // Client has disconnected
            System.out.println("Client disconnected: " + clientChannel.getRemoteAddress());
            clientChannel.close();
            key.cancel();
            return;
        }

        // Process the message
        buffer.flip(); // Prepare the buffer for reading
        String message = new String(buffer.array(), 0, buffer.limit());

        System.out.println("Received message: " + message);

        if (message.equals("*1\r\n$4\r\nPING\r\n")) {
            System.out.println("Valid Redis PING received.");
            clientChannel.write(ByteBuffer.wrap("+PONG\r\n".getBytes()));
            System.out.println("Sent response to client: +PONG");
        } else {
            System.out.println("Unexpected message received.");
        }

        buffer.clear(); // Clear the buffer for the next read
    }
}