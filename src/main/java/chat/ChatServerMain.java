package chat;

import java.io.IOException;


public class ChatServerMain {
    public static final int PORT = 8888;


    public static void main(String[] args) throws IOException, InterruptedException {
        ChatServer chatServer = new ChatServer(PORT);
        chatServer.startServer();
        chatServer.blockUntilShutdown();
    }
}
