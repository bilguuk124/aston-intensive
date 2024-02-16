package com.aston.dz2.repository;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.RequestScoped;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Getter
@RequestScoped
public class ConnectionBean {

    private final String JDBC_URL;
    private static final String USER = "postgres";
    private static final String PASSWORD = "Gungun124";
    private final Logger log = LoggerFactory.getLogger(ConnectionBean.class);
    private Connection connection;

    public ConnectionBean(){
        this.JDBC_URL = "jdbc:postgresql://localhost:8888/aston";
    }
    public ConnectionBean(String url){
        this.JDBC_URL = url;
    }

    @PostConstruct
    public void initialize(){
        try{
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
        } catch (ClassNotFoundException e){
            log.error("JDBC Driver was not found! Exiting...");
            System.exit(1);
        } catch (SQLException e) {
            log.error("Can't connect to database! Message: \n{}", e.getMessage());
            System.exit(1);
        }
    }

    @PreDestroy
    public void closeConnection()  {
        try {
            if (connection != null && !connection.isClosed()){
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
