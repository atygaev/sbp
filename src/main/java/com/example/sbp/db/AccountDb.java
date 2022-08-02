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

public class AccountDb {

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
            connection.setReadOnly(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
//            connection.createStatement().executeQuery("select id,username from users order by id");
            ResultSet coutnResSet = connection.createStatement().executeQuery("select count(*) from accounts");

            coutnResSet.next();
            int count = coutnResSet.getInt(1);

            ArrayList<List<String>> data = new ArrayList<>(count);

            ResultSet dataResSet = connection.createStatement().executeQuery("select id,user_id,amount from accounts order by user_id, id");

            for (int i = 0; i < count; i++) {
                dataResSet.next();
                int id = dataResSet.getInt("id");

                int user_id = dataResSet.getInt("user_id");

                String phone = dataResSet.getString("amount");

                data.add(Arrays.asList(String.valueOf(id), String.valueOf(user_id), phone));
            }

            return data;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<List<String>> fetchAllByPhone(String phone) {
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
            PreparedStatement preparedStatement = connection.prepareStatement("select id, amount from accounts where user_id in (select id from users where phone = ?) order by id");
            preparedStatement.setString(1, phone);

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

    public static void create(int userid, int amount) {
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
            return; // problem with db connect
        }

        try {
            Statement statement = connection.createStatement();
            statement.execute("create table if not exists users(id serial not null primary key, phone text not null unique)");
        } catch (Exception e) {
            e.printStackTrace();
            return; // problem with table
        }

        try {
            Statement statement = connection.createStatement();
            statement.execute("create table if not exists accounts(id serial not null primary key, user_id integer not null, amount integer not null,  CONSTRAINT fk_user\n" +
                    "      FOREIGN KEY(user_id) \n" +
                    "\t  REFERENCES users(id))");
        } catch (Exception e) {
            e.printStackTrace();
            return; // problem with table
        }

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("insert into accounts (user_id, amount) (select id, ? from users where id = ?)");
            preparedStatement.setInt(1, amount);
            preparedStatement.setInt(2, userid);
            preparedStatement.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
