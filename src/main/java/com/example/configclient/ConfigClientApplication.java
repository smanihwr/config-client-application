package com.example.configclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ConfigClientApplication implements CommandLineRunner {

    @Value("${greeting:default}")
    private String greeting;

    public static void main(String[] args) {
        SpringApplication.run(ConfigClientApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Value ::::" + greeting);
    }
}
