package com.txt.order.config;

import com.txt.base.domain.Order;
import com.txt.order.OrderServiceApplication;
import com.txt.order.service.OrderManageService;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.KeyValueBytesStoreSupplier;
import org.apache.kafka.streams.state.Stores;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonSerde;

import java.time.Duration;

@Configuration
public class KafkaConsumerConfig {

    private static final Logger LOG = LoggerFactory.getLogger(OrderServiceApplication.class);

    @Value("${topic.order}")
    private String orderTopic;

    @Value("${topic.payment}")
    private String paymentTopic;

    @Value("${topic.stock}")
    private String stockTopic;

    @Autowired
    OrderManageService orderManageService;

    @Bean
    public KStream<Long, Order> stream(StreamsBuilder builder) {
        JsonSerde<Order> orderSerde = new JsonSerde<>(Order.class);
        KStream<Long, Order> stream = builder
                .stream(paymentTopic, Consumed.with(Serdes.Long(), orderSerde));

        stream.join(builder.stream(stockTopic),
                orderManageService::confirm,
                JoinWindows.of(Duration.ofSeconds(10)),
                StreamJoined.with(Serdes.Long(), orderSerde, orderSerde))
                .peek((k, o) -> LOG.info("Output: {}", o))
                .to(orderTopic);

        return stream;
    }

    @Bean
    public KTable<Long, Order> table(StreamsBuilder builder) {
        KeyValueBytesStoreSupplier store = Stores.persistentKeyValueStore(orderTopic);
        JsonSerde<Order> orderSerde = new JsonSerde<>(Order.class);
        KStream<Long, Order> stream = builder
                .stream(orderTopic, Consumed.with(Serdes.Long(), orderSerde));
        return stream.toTable(Materialized.<Long, Order>as(store)
                .withKeySerde(Serdes.Long())
                .withValueSerde(orderSerde));
    }

}
