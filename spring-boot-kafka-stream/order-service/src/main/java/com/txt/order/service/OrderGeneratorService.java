package com.txt.order.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.txt.base.domain.Order;

import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class OrderGeneratorService {

    private static Random RAND = new Random();
    private AtomicLong id = new AtomicLong();
    private Executor executor;
    private KafkaTemplate<Long, Order> kafkaTemplate;

    @Value("${topic.order}")
    private String orderTopic;

    public OrderGeneratorService(Executor executor, KafkaTemplate<Long, Order> template) {
        this.executor = executor;
        this.kafkaTemplate = template;
    }

    @Async
    public void generate() {
        for (int i = 0; i < 10; i++) {
            int x = RAND.nextInt(5) + 1;
            Order o = new Order(id.incrementAndGet(), RAND.nextLong(100) + 1, RAND.nextLong(100) + 1, "NEW");
            o.setPrice(100 * x);
            o.setProductCount(x);
            kafkaTemplate.send(orderTopic, o.getId(), o);
        }
    }
}
