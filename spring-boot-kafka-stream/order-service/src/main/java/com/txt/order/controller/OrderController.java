package com.txt.order.controller;

import com.txt.order.service.OrderGeneratorService;
import io.swagger.v3.oas.annotations.Operation;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import com.txt.base.domain.Order;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private static final Logger LOG = LoggerFactory.getLogger(OrderController.class);
    private AtomicLong id = new AtomicLong();
    private KafkaTemplate<Long, Order> kafkaTemplate;
    private StreamsBuilderFactoryBean kafkaStreamsFactory;
    private OrderGeneratorService orderGeneratorService;

    @Value("${topic.order}")
    private String orderTopic;

    public OrderController(KafkaTemplate<Long, Order> template,
                           StreamsBuilderFactoryBean kafkaStreamsFactory,
                           OrderGeneratorService orderGeneratorService) {
        this.kafkaTemplate = template;
        this.kafkaStreamsFactory = kafkaStreamsFactory;
        this.orderGeneratorService = orderGeneratorService;
    }

    @Operation(description = "Create one order")
    @PostMapping
    public Order create(@RequestBody Order order) {
        order.setId(id.incrementAndGet());
        kafkaTemplate.send(orderTopic, order.getId(), order);
        LOG.info("Sent: {}", order);
        return order;
    }

    @Operation(description = "Generate many orders")
    @PostMapping("/generate")
    public boolean create() {
        orderGeneratorService.generate();
        return true;
    }

    @Operation(description = "Get all order results")
    @GetMapping
    public List<Order> all() {
        List<Order> orders = new ArrayList<>();
        ReadOnlyKeyValueStore<Long, Order> store = kafkaStreamsFactory
                .getKafkaStreams()
                .store(StoreQueryParameters.fromNameAndType(
                        orderTopic,
                        QueryableStoreTypes.keyValueStore()));
        KeyValueIterator<Long, Order> it = store.all();
        it.forEachRemaining(kv -> orders.add(kv.value));
        return orders;
    }
}
