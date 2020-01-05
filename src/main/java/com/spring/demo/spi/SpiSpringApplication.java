package com.spring.demo.spi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ImportResource("classpath:spring-beans.xml")
public class SpiSpringApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpiSpringApplication.class);
    }

}
