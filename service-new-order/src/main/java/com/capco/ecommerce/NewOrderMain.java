package com.capco.ecommerce;

import com.capco.ecommerce.dispatcher.KafkaDispatcher;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class NewOrderMain {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        try (KafkaDispatcher<Order> orderDispatcher = new KafkaDispatcher<>()) {
            String email = Math.random() + "@email.com";
            for (int i = 0; i < 10; i++) {
                String orderID = UUID.randomUUID().toString();
                BigDecimal value = BigDecimal.valueOf(Math.random() * 5000 + 1);

                Order order = new Order(orderID, value, email);
                orderDispatcher.send("ECOMMERCE_NEW_ORDER", email,
                        new CorrelationId(NewOrderMain.class.getSimpleName()), order);
            }
        }
    }
}
