package by.kutsko;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.concurrent.ExecutionException;

public class Main {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        Client client = context.getBean(Client.class);
        client.run();
    }
}
