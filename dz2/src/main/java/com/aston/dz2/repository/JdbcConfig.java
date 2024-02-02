package com.aston.dz2.repository;

import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JdbcConfig {

    private static final String JDBC_URL = "jdbc:postgresql://localhost:8888/student";
    private static final String USER = "postgres";
    private static final String PASSWORD = "Gungun124";
    private static final Logger log = LoggerFactory.getLogger(JdbcConfig.class);

    static{
        try{
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e){
            log.error("JDBC Driver was not found! Exiting...");
            System.exit(1);
        }
    }

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
        } catch (SQLException e) {
            log.error("Can't connect to database! Message: \n{}", e.getMessage());
        }
        System.exit(1);
        return null;
    }
}
