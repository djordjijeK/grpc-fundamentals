package people;

import com.github.javafaker.Faker;
import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import krivokapic.djordjije.CreatePersonRequest;
import krivokapic.djordjije.Gender;
import krivokapic.djordjije.PeopleServiceGrpc;
import krivokapic.djordjije.PeopleServiceGrpc.PeopleServiceBlockingStub;
import krivokapic.djordjije.Person;
import krivokapic.djordjije.SearchPeopleRequest;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.Scanner;


public class PeopleClient {
    private static final Faker FAKER = new Faker();

    private final PeopleServiceBlockingStub peopleServiceBlockingStub;


    public PeopleClient(String server) {
        this.peopleServiceBlockingStub = PeopleServiceGrpc.newBlockingStub(
                Grpc.newChannelBuilder(server, InsecureChannelCredentials.create())
                        .build()
        );
    }


    public Person createPerson(Person person) {
        return this.peopleServiceBlockingStub.createPerson(CreatePersonRequest.newBuilder()
                .setPerson(person)
                .build()
        );
    }


    public Iterator<Person> getPeople(String searchString) {
        return this.peopleServiceBlockingStub.getPeople(SearchPeopleRequest.newBuilder()
                .setSearchString(searchString)
                .build()
        );
    }


    public static void main(String[] args) {
        PeopleClient peopleClient = new PeopleClient("localhost:8888");

        for (int i = 0; i < 100; i++) {
            Person person = Person.newBuilder()
                    .setName(FAKER.name().fullName())
                    .setAge(FAKER.number().numberBetween(5, 95))
                    .setGender(FAKER.number().numberBetween(0, 10) < 5 ? Gender.MALE : Gender.FEMALE)
                    .build();

            peopleClient.createPerson(person);
        }


        Scanner scanner = new Scanner(System.in);
        PrintStream outputStream = System.out;

        while (true) {
            outputStream.print("search users (type quit to finish): ");
            String input = scanner.nextLine();

            if (input.equalsIgnoreCase("quit")) {
                break;
            }

            System.out.println("-----------------------------------------");
            Iterator<Person> peopleIterator = peopleClient.getPeople(input);
            while (peopleIterator.hasNext()) {
                System.out.println(peopleIterator.next());
            }
            System.out.println("-----------------------------------------");
        }

        scanner.close();
        outputStream.close();
    }
}
