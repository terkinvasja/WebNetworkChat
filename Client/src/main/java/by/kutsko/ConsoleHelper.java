package by.kutsko;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Service
public class ConsoleHelper {

    private static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    public void writeMessage(String message) {
        System.out.println(message);
    }

    public String readString() {
        String message;

        while (true) {
            try {
                message = reader.readLine();
                break;
            } catch (IOException e) {
                System.out.println("Произошла ошибка при попытке ввода текста. Попробуйте еще раз.");
            }
        }
        return message;
    }
}
