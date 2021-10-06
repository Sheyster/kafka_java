package com.capco.ecommerce;

import com.capco.ecommerce.consumer.ConsumerService;
import com.capco.ecommerce.consumer.ServiceRunner;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;

public class ReadingReportService implements ConsumerService<User> {

    private static final Path source = new File("src/main/resources/report.txt").toPath();

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        new ServiceRunner(ReadingReportService::new).start(5);
    }

    public void parse(ConsumerRecord<String, Message<User>> record) throws IOException {
        System.out.println("---------------------------------------------");
        System.out.println("Processing report for " + record.value());
        Message<User> message = record.value();

        User user = message.getPayload();
        File target = new File(user.getReportPath());
        IO.copyTo(source, target);
        IO.append(target, "Created for " + user.getUuid());

        System.out.println("File created: " + target.getAbsolutePath());
    }

    @Override
    public String getTopic() {
        return "ECOMMERCE_USER_GENERATE_READING_REP0RT";
    }

    @Override
    public String getConsumerGroup() {
        return ReadingReportService.class.getSimpleName();
    }
}
