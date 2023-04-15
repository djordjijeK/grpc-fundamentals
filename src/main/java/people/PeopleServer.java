package people;

import io.grpc.Grpc;
import io.grpc.InsecureServerCredentials;
import io.grpc.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;


public class PeopleServer {
    private static final Logger logger = LoggerFactory.getLogger(PeopleServer.class.getName());

    private final int port;
    private final Server server;


    public PeopleServer(int port) {
        this.port = port;
        this.server = Grpc.newServerBuilderForPort(port, InsecureServerCredentials.create())
                .addService(new PeopleService())
                .build();
    }


    private void startServer() throws IOException {
        this.server.start();
        logger.info("People server started, listening on " + this.port);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                System.err.println("ShutdownHook::Shutting down gRPC server since JVM is shutting down");
                try {
                    PeopleServer.this.stopServer();
                } catch (InterruptedException e) {
                    e.printStackTrace(System.err);
                }
                System.err.println("ShutdownHook::Server shut down");
            }
        });
    }


    public void stopServer() throws InterruptedException {
        if (this.server != null) {
            this.server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }


    public void blockUntilShutdown() throws InterruptedException {
        if (this.server != null) {
            this.server.awaitTermination();
        }
    }


    public static void main(String[] args) throws InterruptedException, IOException {
        PeopleServer peopleServer = new PeopleServer(8888);
        peopleServer.startServer();
        peopleServer.blockUntilShutdown();
    }
}
