package com.ricomuh.kasir.kasir;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    public static String url = "jdbc:mysql://localhost:3306/kasir";
    public static String user = "root";
    public static String pass = "";
    public static String driver = "com.mysql.cj.jdbc.Driver";

    public static Connection getConn() {
        Connection conn = null;
        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url, user, pass);
        } catch (ClassNotFoundException e) {
            System.err.println("Database Driver not found: " + e.getMessage());
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            e.printStackTrace();
        }
        if (conn == null) {
            throw new RuntimeException("Failed to establish database connection.");
        }
        return conn;
    }
}
