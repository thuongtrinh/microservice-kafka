package com.txt.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableKafkaStreams
@EnableAsync
public class OrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }

}
