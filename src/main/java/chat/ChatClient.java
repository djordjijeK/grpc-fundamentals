package chat;

import io.grpc.Channel;
import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.stub.StreamObserver;
import krivokapic.djordjije.ChatServiceGrpc;
import krivokapic.djordjije.ChatServiceGrpc.ChatServiceBlockingStub;
import krivokapic.djordjije.ChatServiceGrpc.ChatServiceStub;
import krivokapic.djordjije.JoinRequest;
import krivokapic.djordjije.JoinResponse;
import krivokapic.djordjije.MessageRequest;
import krivokapic.djordjije.MessageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;


public class ChatClient {
    private static final Logger logger = LoggerFactory.getLogger(ChatServer.class);

    private final int port;
    private final String username;
    private final StreamObserver<MessageRequest> requestMessageStream;

    private final ChatServiceStub chatServiceStub;
    private final ChatServiceBlockingStub chatServiceBlockingStub;


    public ChatClient(String username) {
        this.port = ChatServerMain.PORT;
        this.username = username;

        Channel channel = Grpc.newChannelBuilder(String.format("localhost:%s", this.port), InsecureChannelCredentials.create())
                .build();

        this.chatServiceStub = ChatServiceGrpc.newStub(channel);
        this.chatServiceBlockingStub = ChatServiceGrpc.newBlockingStub(channel);

        this.requestMessageStream = this.chatServiceStub.sendMessage(new StreamObserver<>() {
            @Override
            public void onNext(MessageResponse messageResponse) {
                logger.info("${}: {}", messageResponse.getUsername(), messageResponse.getMessage());
            }


            @Override
            public void onError(Throwable t) {
                logger.error(t.getMessage());
            }


            @Override
            public void onCompleted() {
                logger.info("ChatServer is closed");
            }
        });

        this.joinChat();
    }


    private void joinChat() {
        JoinRequest joinRequest = JoinRequest.newBuilder().setUsername(this.username).build();
        JoinResponse joinResponse = this.chatServiceBlockingStub.joinChat(joinRequest);

        StringBuilder stringBuilder = new StringBuilder();
        joinResponse.getUsernameList().forEach(username ->
                stringBuilder.append(username.toLowerCase(Locale.ROOT)).append(", ")
        );

        logger.info("Users present: {}", stringBuilder.toString().trim().substring(0, stringBuilder.length() - 2));
    }


    public void sendMessage(String message) {
        this.requestMessageStream.onNext(MessageRequest.newBuilder()
                .setUsername(username)
                .setMessage(message)
                .build()
        );
    }


    public void closeChat() {
        this.requestMessageStream.onCompleted();
    }
}
