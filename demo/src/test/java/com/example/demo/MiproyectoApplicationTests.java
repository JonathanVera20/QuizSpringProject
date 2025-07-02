package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootTest
@EntityScan("com.example.demo.model")
@EnableJpaRepositories(basePackages = "com.example.demo.repository")
public class MiproyectoApplicationTests {
public MiproyectoApplicationTests() {
}

@Test
public void contextLoads() {
   }
}