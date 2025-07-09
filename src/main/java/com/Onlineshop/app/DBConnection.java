package com.Onlineshop.app;

    import java.sql.Connection;
	import java.sql.DriverManager;
	import java.sql.SQLException;

	public class DBConnection {
	    private static final String URL = "jdbc:postgresql://localhost:5432/onlineshop";
	    private static final String USER = "postgres"; // change if your username is different
	    private static final String PASSWORD = "root"; // replace with your PostgreSQL password

	    public static Connection getConnection() {
	        try {
	            return DriverManager.getConnection(URL, USER, PASSWORD);
	        } catch (SQLException e) {
	            System.out.println("Error connecting to database: " + e.getMessage());
	            return null;
	        }
	    }
	}

