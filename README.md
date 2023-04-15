## gRPC Fundamentals

This repository contains the implementation of two simple projects based on gRPC in Java.
Explanation of projects is given below.

### gRPC

gRPC is a high-performance, open-source framework that is used for building distributed systems. It is based on the Remote Procedure Call (RPC) protocol, which enables
communication between different applications running on different platforms and environments. A Remote Procedure Call is a protocol that enables a program to call a procedure or
function on a remote computer or server.

gRPC uses Protocol Buffers as a language-agnostic binary serialization format that is used to transmit structured data over a network. Protocol Buffers are defined using a special
language called the Protocol Buffer Language. This language is used to define the structure of the data that will be transmitted or stored. Once the structure is defined, a
compiler is used to generate code in different programming languages that can be used to serialize and deserialize data in the Protocol Buffer format.

It uses HTTP/2 as the underlying transport protocol which allows it to efficiently transfer data over the network. HTTP/2 is a binary protocol, which means that it is designed to
transfer data in a binary format rather than plain text. gRPC supports both unary and streaming calls, which means that a client can send a single request and receive multiple
responses or send multiple requests and receive a single
response. This makes gRPC a better choice for applications that require real-time communication or need to transfer large amounts of data over the network.

### People Service with gRPC

The following `.proto` file defines the messages and services used in this simple project:

```protobuf
syntax = "proto3";

option java_multiple_files = true;
option java_outer_classname = "PeopleProto";

package krivokapic.djordjije;


service PeopleService {
  rpc GetPeople(SearchPeopleRequest) returns (stream Person) {}

  rpc CreatePerson(CreatePersonRequest) returns (Person) {}
}

message SearchPeopleRequest {
  string search_string = 1;
}

message CreatePersonRequest {
  Person person = 1;
}

message Person {
  string uuid = 1;
  string name = 2;
  uint32 age = 3;
  Gender gender = 4;
}

enum Gender {
  GENDER_UNDEFINED = 0;
  FEMALE = 1;
  MALE = 2;
}
```

The `PeopleService` service has two methods:

- `GetPeople` returns a stream of `Person` records that match a given search string.


- `CreatePerson` creates a new `Person` record.

#### Server Implementation

- The `PeopleService` class implements the gRPC service defined in the `.proto` file.
  It maintains an in-memory list of `Person` records and provides the implementations for the
  `GetPeople` and `CreatePerson` methods.


- The `getPeople` method filters the list of people records based on the search string provided in the request and returns a stream of matching records to the client.


- The `createPerson` method creates a new `Person` record with a randomly generated UUID and adds it to the list of people records.
  It returns the created record to the client.

#### Client Implementation

- The `PeopleClient` class provides a command-line interface for interacting with the `PeopleService` server.
  It creates a gRPC channel and a blocking stub for communicating with the server.


- The `createPerson` method sends a `CreatePersonRequest` message to the server with the provided `Person` object and returns the created `Person` object (with UUID).


- The `getPeople` method sends a `SearchPeopleRequest` message to the server with the provided search string and returns an iterator that **streams** the matching Person records
  from
  the server.


- The main method of `PeopleClient` creates 100 random `Person` records and adds them to the server using the `createPerson` method.
  It then prompts the user to enter a search string and prints the matching `Person` records returned by the `getPeople` method.

### Chat Application using gRPC

This is a simple chat application implemented using gRPC in Java.
The application allows multiple clients to join a chat room and send messages to each other in real-time.
The communication is implemented using bidirectional streaming, and the application is built on top of gRPC.

The chat application uses the following `.proto` definition:

```protobuf
syntax = "proto3";

option java_multiple_files = true;
option java_outer_classname = "ChatProto";

package krivokapic.djordjije;


service ChatService {
  rpc JoinChat(JoinRequest) returns (JoinResponse) {}

  rpc SendMessage(stream MessageRequest) returns (stream MessageResponse) {}
}


message JoinRequest {
  string username = 1;
}


message JoinResponse {
  repeated string username = 1;
}


message MessageRequest {
  string username = 1;
  string message = 2;
}


message MessageResponse {
  string username = 1;
  string message = 2;
}
```

This proto definition defines the `ChatService` service, which has two methods: `JoinChat` and `SendMessage`.

- `JoinChat` is called when a client joins the chat room. It takes a `JoinRequest` object as input and returns a `JoinResponse` object as output.
  The `JoinRequest` object contains the name of the client who is joining the chat room, while the `JoinResponse` object contains the list of all the usernames of clients currently
  in the chat room.


- `SendMessage` is called when a client sends a message in the chat room. It takes a stream of `MessageRequest` objects as input and returns a stream of `MessageResponse` objects
  as output. The `MessageRequest` object contains the name of the client who sent the message and the message itself, while the `MessageResponse` object contains the name of the
  client who sent the message and the message itself.
  This message is pushed to all participants in the chat.

#### Server Implementation

The server implementation is defined in the `ChatService` class, which extends the `ChatServiceImplBase` class.
The `ChatService` class has two methods: `joinChat` and `sendMessage`.

- `joinChat` is called when a client joins the chat room. It adds the client's username to a set of usernames and sends a message to all the clients in the chat room to inform them
  that a new client has joined. It then returns the list of all the usernames of clients currently in the chat room.


- `sendMessage` is called when a client sends a message in the chat room. It adds the client's stream observer to a set of stream observers, which are used to send messages to all
  the clients in the chat room. It also sends the client's message to all the clients in the chat room.

#### Client Implementation

The client implementation is defined in the `ChatClient` class. The `ChatClient` class is responsible for creating a gRPC channel to the server and sending requests to the
server. It has two methods: `joinChat` and `sendMessage`.

- `joinChat` sends a `JoinRequest` object to the server to join the chat room. It then receives a `JoinResponse` object from the server, which contains the list of all the
  usernames of
  clients currently in the chat room.


- `sendMessage` sends a `MessageRequest` object to the server to send a message to all the clients in the chat room. It receives a stream of `MessageResponse` objects from the
  server,
  which contains the name of the client who sent the message and the message itself.