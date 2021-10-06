package com.capco.ecommerce.consumer;

public interface ServiceFactory<T> {
    ConsumerService<T> create();
}
