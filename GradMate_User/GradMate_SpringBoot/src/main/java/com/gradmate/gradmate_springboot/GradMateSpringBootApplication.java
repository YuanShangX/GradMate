package com.gradmate.gradmate_springboot;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.gradmate.gradmate_springboot.mapper")
public class GradMateSpringBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(GradMateSpringBootApplication.class, args);
    }

}
