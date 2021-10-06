package com.capco.ecommerce.consumer;

import com.capco.ecommerce.Message;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public interface ConsumerFunction<T> {
    void consumer(ConsumerRecord<String, Message<T>> record) throws Exception;
}
