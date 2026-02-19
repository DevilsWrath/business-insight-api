package de.savas.businessinsightapi;

import de.savas.businessinsightapi.infrastructure.security.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;


@SpringBootApplication
@EnableConfigurationProperties(JwtProperties.class)
public class BusinessInsightApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(BusinessInsightApiApplication.class, args);
    }

}
