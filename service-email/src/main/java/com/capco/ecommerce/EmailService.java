package com.capco.ecommerce;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.HashMap;

public class EmailService {

    public static void main(String[] args) {
        EmailService emailService = new EmailService();
        try (KafkaService service = new KafkaService(EmailService.class.getName(), "ECOMMERCE_SEND_EMAIL",
                emailService::parse, String.class,
                new HashMap<String, String>())) {
            service.run();
        }
    }

    private void parse(ConsumerRecord<String, String> record) {
        System.out.println("---------------------------------------------");
        System.out.println("Send email");
        System.out.println(record.key());
        System.out.println(record.value());
        System.out.println(record.partition());
        System.out.println(record.offset());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Email sended");
    }

}
