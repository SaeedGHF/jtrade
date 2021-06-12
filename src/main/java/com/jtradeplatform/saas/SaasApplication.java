package com.jtradeplatform.saas;

import org.apache.log4j.BasicConfigurator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SaasApplication {

    public static void main(String[] args) {
        BasicConfigurator.configure();
        SpringApplication.run(SaasApplication.class, args);
    }
}
