package com.example.sbp.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class UserDb {

    public static int saveUser(String phone) {
        String db_url = System.getenv("DB_URL");

        if (db_url == null) {
            db_url = "jdbc:postgresql://localhost:5432/postgres?user=postgres&password=postgres";
        }

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        Connection connection;
        try {
            connection = DriverManager.getConnection(db_url);
        } catch (SQLException e) {
            e.printStackTrace();
            return 500; // problem with db connect
        }

        try {
            Statement statement = connection.createStatement();
            statement.execute("create table if not exists users(id serial not null primary key, phone text not null unique)");
        } catch (Exception e) {
            e.printStackTrace();
            return 500; // problem with table
        }

        try {
            if (!connection.getAutoCommit()) {
                connection.setAutoCommit(true);
            }

            PreparedStatement preparedStatement = connection.prepareStatement("insert into users(phone) values (?)");
            preparedStatement.setString(1, phone);
            preparedStatement.execute();
        } catch (Exception e) {
            e.printStackTrace();
            return 409; // user exists by username
        }

        try {
            return 200; // everything alright
        } finally {
            try {
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static List<List<String>> fetchAll() {
        String db_url = System.getenv("DB_URL");

        if (db_url == null) {
            db_url = "jdbc:postgresql://localhost:5432/postgres?user=postgres&password=postgres";
        }

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        Connection connection;
        try {
            connection = DriverManager.getConnection(db_url);
        } catch (SQLException e) {
            e.printStackTrace();
            return Collections.EMPTY_LIST; // problem with db connect
        }

        try {
            Statement statement = connection.createStatement();
            statement.execute("create table if not exists users(id serial not null primary key, phone text not null unique)");
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.EMPTY_LIST; // problem with table
        }

        try {
            connection.setReadOnly(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try {
//            connection.createStatement().executeQuery("select id,username from users order by id");
            ResultSet coutnResSet = connection.createStatement().executeQuery("select count(*) from users");

            coutnResSet.next();
            int count = coutnResSet.getInt(1);

            ArrayList<List<String>> data = new ArrayList<>(count);

            ResultSet dataResSet = connection.createStatement().executeQuery("select id,phone from users order by id");

            for (int i = 0; i < count; i++) {
                dataResSet.next();
                int id = dataResSet.getInt("id");

                String un = dataResSet.getString("phone");

                data.add(Arrays.asList(String.valueOf(id), un));
            }

            return data;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<List<String>> fetchUserAccounts(int userId) {
        String db_url = System.getenv("DB_URL");

        if (db_url == null) {
            db_url = "jdbc:postgresql://localhost:5432/postgres?user=postgres&password=postgres";
        }

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        Connection connection;
        try {
            connection = DriverManager.getConnection(db_url);
        } catch (SQLException e) {
            e.printStackTrace();
            return Collections.EMPTY_LIST; // problem with db connect
        }

        try {
            Statement statement = connection.createStatement();
            statement.execute("create table if not exists users(id serial not null primary key, phone text not null unique)");
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.EMPTY_LIST; // problem with table
        }

        try {
            Statement statement = connection.createStatement();
            statement.execute("create table if not exists accounts(id serial not null primary key, user_id integer not null, amount integer not null,  CONSTRAINT fk_user\n" +
                    "      FOREIGN KEY(user_id) \n" +
                    "\t  REFERENCES users(id))");
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.EMPTY_LIST; // problem with table
        }

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("select id, amount from accounts where user_id = ? order by id");
            preparedStatement.setInt(1, userId);

            List<List<String>> data = new ArrayList<>();

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                data.add(Arrays.asList(String.valueOf(resultSet.getInt("id")), resultSet.getString("amount")));
            }

            return data;
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.EMPTY_LIST;
        }
    }
}
