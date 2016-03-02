package com.github.putpixel.arvato;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.google.common.collect.ImmutableMap;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(Application.class);
        ImmutableMap<String, Object> params = ImmutableMap.of(
                "server.port", "8888",
                "spring.jpa.hibernate.ddl-auto", "create-drop");
        app.setDefaultProperties(params);
        app.run(args);
    }
}
