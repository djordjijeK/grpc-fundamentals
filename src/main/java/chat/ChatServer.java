package chat;

import io.grpc.Grpc;
import io.grpc.InsecureServerCredentials;
import io.grpc.Server;

import java.io.IOException;
import java.util.concurrent.TimeUnit;


public class ChatServer {
    private final int port;
    private final Server server;


    public ChatServer(int port) {
        this.port = port;
        this.server = Grpc.newServerBuilderForPort(this.port, InsecureServerCredentials.create())
                .addService(new ChatService())
                .build();
    }


    public void startServer() throws IOException {
        this.server.start();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                System.err.println("Shutting down gRPC server since JVM is shutting down");
                try {
                    ChatServer.this.stopServer();
                } catch (InterruptedException e) {
                    e.printStackTrace(System.err);
                }
                System.err.println("Server shut down");
            }
        });
    }


    public void blockUntilShutdown() throws InterruptedException {
        if (this.server != null) {
            this.server.awaitTermination();
        }
    }


    private void stopServer() throws InterruptedException {
        if (this.server != null) {
            this.server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }
}
