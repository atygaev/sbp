package com.example.sbp.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class BillingDb {
    public static void makePayment(int acc1, int acc2, int summa) {
        /**
         * update
         *     accounts
         * set
         *     amount = (case
         *                   when id = 111 then amount + 999
         *                   when id = 333 then amount - 999
         *         end)
         * where
         *         id in (111, 333);
         */

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
            if (!connection.getAutoCommit()) {
                connection.setAutoCommit(true);
            }

            String sql =
                    " update" +
                            " accounts" +
                            " set" +
                            " amount = (case" +
                            " when id = <acc1> then amount - <delta>" +
                            " when id = <acc2> then amount + <delta>" +
                            " end)" +
                            " where" +
                            " id in (<acc1>, <acc2>)";

            PreparedStatement preparedStatement = connection.prepareStatement(sql
                    .replace("<acc1>", String.valueOf(acc1))
                    .replace("<acc2>", String.valueOf(acc2))
                    .replace("<delta>", String.valueOf(summa))
            );
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
