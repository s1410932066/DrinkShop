package com.example.testdatabase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfig {
    public static final String URL = "jdbc:jtds:sqlserver://192.168.1.249:1433/DrinkShop";
    public static final String USERNAME = "sa";
    public static final String PASSWORD = "2ixxddux";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }
}
