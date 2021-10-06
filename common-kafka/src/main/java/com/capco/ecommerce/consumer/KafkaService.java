package com.capco.ecommerce.consumer;

import com.capco.ecommerce.Message;
import com.capco.ecommerce.dispatcher.GsonSerializer;
import com.capco.ecommerce.dispatcher.KafkaDispatcher;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.io.Closeable;
import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

public class KafkaService<T> implements Closeable {
    private final KafkaConsumer<String, Message<T>> consumer;
    private final ConsumerFunction parse;

    public KafkaService(String groupId, String topic, ConsumerFunction<T> parse, Map<String, String> properties) {
        this(parse, groupId, properties);
        consumer.subscribe(Collections.singletonList(topic));
    }

    public KafkaService(String groupId, Pattern topic, ConsumerFunction<T> parse, Map<String, String> properties) {
        this(parse, groupId, properties);
        consumer.subscribe(topic);
    }

    private KafkaService(ConsumerFunction<T> parse, String groupId, Map<String, String> properties) {
        this.parse = parse;
        this.consumer = new KafkaConsumer<>(getProperties(groupId, properties));
    }

    public void run() throws ExecutionException, InterruptedException {
        try(KafkaDispatcher deadLetter = new KafkaDispatcher<>()) {
            while (true) {
                ConsumerRecords<String, Message<T>> records = consumer.poll(Duration.ofMillis(100));
                if (!records.isEmpty()) {
                    System.out.println("Encotrei " + records.count() + " registros");

                    for (ConsumerRecord record : records) {
                        try {
                            parse.consumer(record);
                        } catch (Exception e) {
                            // only catches Exception because no matter witch Exception
                            // I want to recover and parse the next one
                            // so far, just logging the exception for this message
                            e.printStackTrace();
                            // treatment now
                            Message<T> message = (Message<T>) record.value();
                            deadLetter.send("ECOMMERCE_DEADLETTER", message.getId().toString(),
                                    message.getId().continueWith("DeadLetter"),
                                    new GsonSerializer<>().serialize("", message));
                        }
                    }
                }
            }
        }
    }

    private Properties getProperties(String groupId, Map<String, String> overrideProperties) {
        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "172.24.132.133:9092");
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, GsonDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        //Id generated to never break in one message, because if you send the same message always, the consumer always be the same too
        properties.setProperty(ConsumerConfig.CLIENT_ID_CONFIG, UUID.randomUUID().toString());
        properties.setProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "1"); //Max message process
        properties.putAll(overrideProperties);

        return properties;
    }

    @Override
    public void close() {
        consumer.close();
    }
}
