package com.txt.kafka.stock.service;

import com.txt.kafka.stock.model.Order;
import com.txt.kafka.stock.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    private OrderRepository orderRepository;

    public OrderService(OrderRepository repository) {
        this.orderRepository = repository;
    }

    public Order add(Order order) {
        return orderRepository.save(order);
    }

    @Transactional
    public boolean performUpdate(Long buyOrderId, Long sellOrderId, int amount) {
        Order buyOrder = orderRepository.findById(buyOrderId).orElseThrow();
        Order sellOrder = orderRepository.findById(sellOrderId).orElseThrow();
        int buyAvailableCount = buyOrder.getProductCount() - buyOrder.getRealizedCount();
        int sellAvailableCount = sellOrder.getProductCount() - sellOrder.getRealizedCount();

        if (buyAvailableCount >= amount && sellAvailableCount >= amount) {
            buyOrder.setRealizedCount(buyOrder.getRealizedCount() + amount);
            sellOrder.setRealizedCount(sellOrder.getRealizedCount() + amount);
            orderRepository.save(buyOrder);
            orderRepository.save(sellOrder);
            return true;
        } else {
            return false;
        }
    }
}
