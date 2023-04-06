package com.txt.order.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import com.txt.order.OrderServiceApplication;

import java.util.concurrent.Executor;

@Configuration
public class KafkaConfig {

    private static final Logger LOG = LoggerFactory.getLogger(OrderServiceApplication.class);

    @Value("${kafka.topic.num-partitions}")
    private String partitions;

    @Value("${kafka.topic.replication-factor}")
    private String replications;

    @Value("${topic.order}")
    private String orderTopic;

    @Value("${topic.payment}")
    private String paymentTopic;

    @Value("${topic.stock}")
    private String stockTopic;

    @Bean
    public NewTopic orders() {
        return TopicBuilder.name(orderTopic)
                .partitions(Integer.valueOf(partitions))
                .replicas(Integer.valueOf(replications))
                .compact()
                .build();
    }

    @Bean
    public NewTopic paymentTopic() {
        return TopicBuilder.name(paymentTopic)
                .partitions(Integer.valueOf(partitions))
                .replicas(Integer.valueOf(replications))
                .compact()
                .build();
    }

    @Bean
    public NewTopic stockTopic() {
        return TopicBuilder.name(stockTopic)
                .partitions(Integer.valueOf(partitions))
                .replicas(Integer.valueOf(replications))
                .compact()
                .build();
    }

    @Bean
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(5);
        executor.setThreadNamePrefix("kafkaSender-");
        executor.initialize();
        return executor;
    }
}
