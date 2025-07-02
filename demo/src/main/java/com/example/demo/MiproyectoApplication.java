package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.example.demo", "com.example.demo.config"})
public class MiproyectoApplication {

    public static void main(String[] args) {
        SpringApplication.run(  MiproyectoApplication.class, args);
    }
}
