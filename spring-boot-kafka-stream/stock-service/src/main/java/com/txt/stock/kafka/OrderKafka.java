package com.txt.stock.kafka;

import com.txt.stock.StockServiceApplication;
import com.txt.stock.domain.Product;
import com.txt.stock.repository.ProductRepository;
import com.txt.stock.service.OrderManageService;
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

    private static final Logger LOG = LoggerFactory.getLogger(StockServiceApplication.class);

    @Autowired
    OrderManageService orderManageService;

    @Autowired
    private ProductRepository repository;

    @KafkaListener(id = "orders", topics = "${topic.order}", groupId = "stock")
    public void onEvent(Order o) {
        LOG.info("Received: {}" , o);
        if (o.getStatus().equals("NEW"))
            orderManageService.reserve(o);
        else
            orderManageService.confirm(o);
    }

    @PostConstruct
    public void generateData() {
        Random r = new Random();
        for (int i = 0; i < 1000; i++) {
            int count = r.nextInt(1000);
            Product p = new Product(null, "Product" + i, count, 0);
            repository.save(p);
        }
    }
}
