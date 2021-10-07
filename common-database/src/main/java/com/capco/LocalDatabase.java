package com.capco;

import java.sql.*;

public class LocalDatabase {

    private final Connection connection;

    public LocalDatabase(String name) throws SQLException {
        String url = "jdbc:sqlite:target/" + name + ".db";
        this.connection = DriverManager.getConnection(url);
    }

    //yes, this is way too generic
    // according to your database tool, avoid injection
    public void createIfNotExists(String sql){
        try {
            connection.createStatement().execute(sql);
        } catch (SQLException ex) {
            // be careful, the SQL could be wrong, be really carefull
            ex.printStackTrace();
        }
    }

    public void update(String statement, String... params) throws SQLException {
        PreparedStatement preparedStatement = prepare(statement, params);
        preparedStatement.execute();
    }

    public ResultSet query(String query, String... params) throws SQLException {
        PreparedStatement preparedStatement = prepare(query, params);
        return preparedStatement.executeQuery();
    }

    private PreparedStatement prepare(String statement, String[] params) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(statement);
        for (int i = 0; i < params.length; i++) {
            preparedStatement.setString(i + 1, params[i]);
        }
        return preparedStatement;
    }
}
