package com.example.dbsave;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ApplicationContext;

import java.io.IOException;

@SpringBootApplication
public class DbSaveApplication {
    public static void main(String[] args) throws IOException {
        ApplicationContext context = SpringApplication.run(DbSaveApplication.class, args);

        // Spring 컨텍스트에서 MaridDBSave 빈을 가져옴
        MaridDBSave maridDBSave = context.getBean(MaridDBSave.class);

        // start 메소드 호출
        maridDBSave.start();
    }

}
