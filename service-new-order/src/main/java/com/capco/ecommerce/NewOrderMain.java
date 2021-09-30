package com.capco.ecommerce;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class NewOrderMain {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        try (KafkaDispatcher<Order> orderDispatcher = new KafkaDispatcher()) {
            try (KafkaDispatcher<Email> emailDispatcher = new KafkaDispatcher()) {
                String email = Math.random() + "@email.com";
                for (int i = 0; i < 10; i++) {
//                    String userID = UUID.randomUUID().toString();
                    String orderID = UUID.randomUUID().toString();
                    BigDecimal value = new BigDecimal(Math.random() * 5000 + 1);

                    Order order = new Order(orderID, value, email);
                    orderDispatcher.send("ECOMMERCE_NEW_ORDER", email, order);

                    Email emailCode = new Email("Thanks", "Thank you for your order! We are processing your order!");
                    emailDispatcher.send("ECOMMERCE_SEND_EMAIL", email, emailCode);
                }
            }
        }
    }
}
