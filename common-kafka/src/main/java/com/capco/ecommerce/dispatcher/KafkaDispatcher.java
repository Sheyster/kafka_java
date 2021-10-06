package com.capco.ecommerce.dispatcher;

import com.capco.ecommerce.CorrelationId;
import com.capco.ecommerce.Message;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.io.Closeable;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class KafkaDispatcher<T> implements Closeable {

    private final KafkaProducer<String, Message<T>> producer;

    public KafkaDispatcher() {
        this.producer = new KafkaProducer<>(properties());
    }

    private static Properties properties() {
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "172.24.132.133:9092");
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, GsonSerializer.class.getName());
        properties.setProperty(ProducerConfig.ACKS_CONFIG, "all");

        return properties;
    }

    // Send and wait for an answer "Sync"
    public void send(String topic, String key, CorrelationId id, T payload) throws ExecutionException, InterruptedException {
        Future future = sendAsync(topic, key, id, payload);
        future.get(); // Desta forma ele ira adicionar o throws e rodar de forma sincrona
    }

    // Send and not wait for an answer "Async"
    public Future sendAsync(String topic, String key, CorrelationId id, T payload) {
        Message value = new Message(id, payload);
        ProducerRecord record = new ProducerRecord(topic, key, value);
        Callback callback = (data, ex) -> {
            if (ex != null) {
                ex.printStackTrace();
                return;
            }
            System.out.println(data.topic() + "::: partition " + data.partition() + "/offset " + data.offset() + "/timestamp " + data.timestamp());
        };

        //producer.send(record); // Desta forma ele roda de forma assincrona
        Future future = producer.send(record, callback);
        return future;
    }

    @Override
    public void close() {
        producer.close();
    }
}
