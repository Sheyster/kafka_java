package com.capco.ecommerce;

public interface ServiceFactory<T> {
    ConsumerService<T> create();
}
