package chat;

import io.grpc.stub.StreamObserver;
import krivokapic.djordjije.ChatServiceGrpc.ChatServiceImplBase;
import krivokapic.djordjije.JoinRequest;
import krivokapic.djordjije.JoinResponse;
import krivokapic.djordjije.MessageRequest;
import krivokapic.djordjije.MessageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


public class ChatService extends ChatServiceImplBase {
    static Logger logger = LoggerFactory.getLogger(ChatService.class);

    private final Set<String> users;
    private final Set<StreamObserver<MessageResponse>> observers;


    public ChatService() {
        this.users = ConcurrentHashMap.newKeySet();
        this.observers = ConcurrentHashMap.newKeySet();
    }


    @Override
    public void joinChat(JoinRequest request, StreamObserver<JoinResponse> responseObserver) {
        this.users.add(request.getUsername());
        logger.info("New user joined the chat: {}", request.getUsername());

        MessageResponse messageResponse = MessageResponse.newBuilder()
                .setUsername(request.getUsername())
                .setMessage(String.format("User %s joined the chat!", request.getUsername()))
                .build();
        for (StreamObserver<MessageResponse> observer : observers) {
            observer.onNext(messageResponse);
        }

        responseObserver.onNext(
                JoinResponse.newBuilder()
                        .addAllUsername(this.users)
                        .build()
        );
        responseObserver.onCompleted();
    }


    @Override
    public StreamObserver<MessageRequest> sendMessage(StreamObserver<MessageResponse> responseObserver) {
        this.observers.add(responseObserver);

        return new StreamObserver<>() {
            private String username;


            @Override
            public void onNext(MessageRequest messageRequest) {
                username = messageRequest.getUsername().toLowerCase(Locale.ROOT);

                MessageResponse messageResponse = MessageResponse.newBuilder()
                        .setUsername(messageRequest.getUsername())
                        .setMessage(messageRequest.getMessage())
                        .build();

                for (StreamObserver<MessageResponse> observer : observers) {
                    observer.onNext(messageResponse);
                }
            }


            @Override
            public void onError(Throwable t) {
                logger.error(t.getMessage());
            }


            @Override
            public void onCompleted() {
                users.remove(username);
                observers.remove(responseObserver);

                logger.info("User {} left the chat", username);

                MessageResponse messageResponse = MessageResponse.newBuilder()
                        .setUsername(username)
                        .setMessage(String.format("User %s left the chat!", username))
                        .build();
                for (StreamObserver<MessageResponse> observer : observers) {
                    observer.onNext(messageResponse);
                }
            }
        };
    }
}
