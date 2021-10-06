package com.capco.ecommerce;

import com.capco.ecommerce.consumer.KafkaService;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.sql.*;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class CreateUserService {

    private final Connection connection;

    CreateUserService() throws SQLException {
        String url = "jdbc:sqlite:target/users_database.db";
        this.connection = DriverManager.getConnection(url);
        try {
            connection.createStatement().execute("create table Users (" +
                    "uuid varchar(200) primary key, " +
                    "email varchar(200))");
        } catch (SQLException ex) {
            // be careful, the SQL could be wrong, be really carefull
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) throws SQLException, ExecutionException, InterruptedException {
        CreateUserService createUserService = new CreateUserService();
        try (KafkaService<Order> service = new KafkaService(CreateUserService.class.getSimpleName(), "ECOMMERCE_NEW_ORDER",
                createUserService::parse,
                new HashMap<String, String>())) {
            service.run();
        }
    }

    private void parse(ConsumerRecord<String, Message<Order>> record) throws SQLException {
        System.out.println("---------------------------------------------");
        System.out.println("Processing new order, checking for new user");
        System.out.println(record.value());
        Order order = record.value().getPayload();
        if (isNewUser(order.getEmail())) {
            insertNewUser(order.getEmail());
        }
    }

    private void insertNewUser(String email) throws SQLException {
        PreparedStatement insert = connection.prepareStatement("insert into Users (uuid, email) " +
                "values (?, ?)");

        insert.setString(1, UUID.randomUUID().toString());
        insert.setString(2, email);
        insert.execute();

        System.out.println("User uuid and " + email + " add");
    }

    private boolean isNewUser(String email) throws SQLException {
        PreparedStatement exists = connection.prepareStatement("select uuid from Users " +
                "where email = ? limit 1");
        exists.setString(1, email);
        ResultSet results = exists.executeQuery();
        return !results.next();
    }
}
