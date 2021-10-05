package com.capco.ecommerce;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;

public class ReadingReportService {

    private static final Path source = new File("src/main/resources/report.txt").toPath();

    public static void main(String[] args) {
        ReadingReportService readingReportService = new ReadingReportService();
        try (KafkaService<User> service = new KafkaService(
                ReadingReportService.class.getSimpleName(), " ECOMMERCE_USER_GENERATE_READING_REP0RT",
                readingReportService::parse, User.class,
                new HashMap<String, String>())) {
            service.run();
        }
    }

    private void parse(ConsumerRecord<String, Message<User>> record) throws IOException {
        System.out.println("---------------------------------------------");
        System.out.println("Processing report for " + record.value());
        Message<User> message = record.value();

        User user = message.getPayload();
        File target = new File(user.getReportPath());
        IO.copyTo(source, target);
        IO.append(target, "Created for " + user.getUuid());

        System.out.println("File created: " + target.getAbsolutePath());
    }

}
