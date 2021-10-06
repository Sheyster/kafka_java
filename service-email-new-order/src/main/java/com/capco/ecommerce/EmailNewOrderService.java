package com.capco.ecommerce;

import com.capco.ecommerce.consumer.ConsumerService;
import com.capco.ecommerce.consumer.KafkaService;
import com.capco.ecommerce.consumer.ServiceRunner;
import com.capco.ecommerce.dispatcher.KafkaDispatcher;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class EmailNewOrderService implements ConsumerService<Order> {

    private final KafkaDispatcher<String> emailDispatcher = new KafkaDispatcher();

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        new ServiceRunner(EmailNewOrderService::new).start(1);
    }

    public void parse(ConsumerRecord<String, Message<Order>> record) throws ExecutionException, InterruptedException {
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

    @Override
    public String getTopic() {
        return "ECOMMERCE_NEW_ORDER";
    }

    @Override
    public String getConsumerGroup() {
        return EmailNewOrderService.class.getSimpleName();
    }
}
