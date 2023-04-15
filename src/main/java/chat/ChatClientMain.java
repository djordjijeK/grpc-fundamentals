package chat;

import java.io.PrintStream;
import java.util.Scanner;


public class ChatClientMain {


    public static void main(String[] args) {
        ChatClient chatClient = new ChatClient("username");

        Scanner scanner = new Scanner(System.in);
        PrintStream outputStream = System.out;

        while (true) {
            String input = scanner.nextLine();

            if (input.equalsIgnoreCase("quit")) {
                chatClient.closeChat();
                break;
            }

            chatClient.sendMessage(input);
        }

        scanner.close();
        outputStream.close();
    }

}
