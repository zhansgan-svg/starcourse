package com.starcourse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class StarCourseApplication {

    public static void main(String[] args) {
        SpringApplication.run(StarCourseApplication.class, args);
    }
}
