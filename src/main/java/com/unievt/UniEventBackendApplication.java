package com.unievt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class UniEventBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(UniEventBackendApplication.class, args);
    }

}
