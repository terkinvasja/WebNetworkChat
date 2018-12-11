package by.kutsko.config;

import by.kutsko.server.ServerCondition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public ServerCondition getServerCondition() {
        return new ServerCondition();
    }
}
