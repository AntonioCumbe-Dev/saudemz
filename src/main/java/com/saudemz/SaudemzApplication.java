
package com.saudemz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SaudemzApplication {

    public static void main(String[] args) {
        SpringApplication.run(SaudemzApplication.class, args);
    }
}
