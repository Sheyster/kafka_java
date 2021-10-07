package com.capco.ecommerce.consumer;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

public class ServiceProvider<T> implements Callable<Void> {
    private final ServiceFactory<T> factory;

    public ServiceProvider(ServiceFactory<T> factory) {
        this.factory = factory;
    }

    public Void call() throws ExecutionException, InterruptedException, SQLException {
        ConsumerService<?> myService = factory.create();
        try (KafkaService<?> service = new KafkaService<>(myService.getConsumerGroup(),
                myService.getTopic(),
                myService::parse,
                new HashMap<>())) {
            service.run();
        }
        return null;
    }
}
