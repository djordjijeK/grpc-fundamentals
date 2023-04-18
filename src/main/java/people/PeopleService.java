package people;

import io.grpc.stub.StreamObserver;
import krivokapic.djordjije.CreatePersonRequest;
import krivokapic.djordjije.PeopleServiceGrpc;
import krivokapic.djordjije.Person;
import krivokapic.djordjije.SearchPeopleRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.UUID;


public class PeopleService extends PeopleServiceGrpc.PeopleServiceImplBase {
    private static final Logger logger = LoggerFactory.getLogger(PeopleService.class.getName());

    private final List<Person> people;


    public PeopleService() {
        this.people = Collections.synchronizedList(new ArrayList<>(100));
    }


    @Override
    public void getPeople(SearchPeopleRequest request, StreamObserver<Person> responseObserver) {
        logger.info(String.format("SearchRequest - %s", request.toString()));

        this.people.forEach(person -> {
            if (person.getName().toLowerCase(Locale.ROOT).contains(request.getSearchString().toLowerCase(Locale.ROOT))) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                responseObserver.onNext(person);
            }
        });

        responseObserver.onCompleted();
    }


    @Override
    public void createPerson(CreatePersonRequest request, StreamObserver<Person> responseObserver) {
        Person newPerson = Person.newBuilder()
                .setUuid(UUID.randomUUID().toString())
                .setName(request.getPerson().getName())
                .setAge(request.getPerson().getAge())
                .setGender(request.getPerson().getGender())
                .build();

        logger.info(String.format("Created new person:\n\n%s", newPerson));
        this.people.add(newPerson);

        responseObserver.onNext(newPerson);
        responseObserver.onCompleted();
    }
}
