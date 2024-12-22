package com.kesehatan;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/klinik";
    private static final String USER = "root";  // Ganti dengan username Anda jika perlu
    private static final String PASSWORD = "";  // Ganti dengan password Anda jika perlu

    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");  // Pastikan driver di-load
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

}



