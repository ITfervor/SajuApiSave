package com.example.dbsave;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class DbSaveApplication {

    public static void main(String[] args) {
        SpringApplication.run(DbSaveApplication.class, args);
    }

}
