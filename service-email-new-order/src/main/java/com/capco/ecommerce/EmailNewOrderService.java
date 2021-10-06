package com.capco.ecommerce;

import com.capco.ecommerce.consumer.KafkaService;
import com.capco.ecommerce.dispatcher.KafkaDispatcher;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class EmailNewOrderService {

    private final KafkaDispatcher<String> emailDispatcher = new KafkaDispatcher();

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        EmailNewOrderService emailService = new EmailNewOrderService();
        try (KafkaService<Order> service = new KafkaService(EmailNewOrderService.class.getSimpleName(), "ECOMMERCE_NEW_ORDER",
                emailService::parse,
                new HashMap<String, String>())) {
            service.run();
        }
    }

    private void parse(ConsumerRecord<String, Message<Order>> record) throws ExecutionException, InterruptedException {
        System.out.println("---------------------------------------------");
        System.out.println("Processing new order, preparing email");
        System.out.println(record.value());

        Order order = record.value().getPayload();
//        Email emailCode = new Email("Thanks", "Thank you for your order! We are processing your order!");
        String emailCode = "Thank you for your order! We are processing your order!";
        emailDispatcher.send("ECOMMERCE_SEND_EMAIL", order.getEmail(),
                record.value().getId().continueWith(EmailNewOrderService.class.getSimpleName()),
                emailCode);

    }
}
