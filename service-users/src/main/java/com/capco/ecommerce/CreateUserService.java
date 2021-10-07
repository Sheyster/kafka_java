package com.capco.ecommerce;

import com.capco.LocalDatabase;
import com.capco.ecommerce.consumer.ConsumerService;
import com.capco.ecommerce.consumer.ServiceRunner;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.sql.*;
import java.util.UUID;

public class CreateUserService implements ConsumerService<Order> {

    private final LocalDatabase database;

    private CreateUserService() throws SQLException {
        this.database = new LocalDatabase("users_database");
        this.database.createIfNotExists("create table Users (" +
                "uuid varchar(200) primary key, " +
                "email varchar(200))");
    }

    public static void main(String[] args) {
        new ServiceRunner<>(CreateUserService::new).start(1);
    }

    public void parse(ConsumerRecord<String, Message<Order>> record) throws SQLException {
        System.out.println("---------------------------------------------");
        System.out.println("Processing new order, checking for new user");
        System.out.println(record.value());
        Order order = record.value().getPayload();
        if (isNewUser(order.getEmail())) {
            insertNewUser(order.getEmail());
        }
    }

    @Override
    public String getTopic() {
        return "ECOMMERCE_NEW_ORDER";
    }

    @Override
    public String getConsumerGroup() {
        return CreateUserService.class.getSimpleName();
    }

    private void insertNewUser(String email) throws SQLException {
        String uuid = UUID.randomUUID().toString();
        database.update("insert into Users (uuid, email) " +
                "values (?, ?)", uuid,email);
        System.out.println("User " + uuid + " and " + email + " add");
//
//
//        PreparedStatement insert = connection.prepareStatement();
//
//        insert.setString(1, );
//        insert.setString(2, email);
//        insert.execute();
//
//        System.out.println("User uuid and " + email + " add");
    }

    private boolean isNewUser(String email) throws SQLException {

        ResultSet results = database.query("select uuid from Users " +
                "where email = ? limit 1", email);
//        PreparedStatement exists = connection.prepareStatement();
//        exists.setString(1, email);
//        ResultSet results = exists.executeQuery();
        return !results.next();
    }
}
