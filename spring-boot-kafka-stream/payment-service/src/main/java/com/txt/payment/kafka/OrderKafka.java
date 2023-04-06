package com.txt.payment.kafka;

import com.txt.payment.PaymentServiceApplication;
import com.txt.payment.domain.Customer;
import com.txt.payment.repository.CustomerRepository;
import com.txt.payment.service.OrderManageService;
import net.datafaker.Faker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import com.txt.base.domain.Order;

import javax.annotation.PostConstruct;
import java.util.Random;

@Service
public class OrderKafka {

    private static final Logger LOG = LoggerFactory.getLogger(PaymentServiceApplication.class);

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    OrderManageService orderManageService;

    @KafkaListener(id = "orders", topics = "${topic.order}", groupId = "payment")
    public void onEvent(Order o) {
        LOG.info("Received: {}" , o);
        if (o.getStatus().equals("NEW")) {
            orderManageService.reserve(o);
        } else {
            orderManageService.confirm(o);
        }
    }

    @PostConstruct
    public void generateData() {
        Random r = new Random();
        Faker faker = new Faker();
        for (int i = 0; i < 100; i++) {
            int count = r.nextInt(1000);
            Customer c = new Customer(null, faker.name().fullName(), count, 0);
            customerRepository.save(c);
        }
    }
}
