package com.QuoraUpGrad.quora.api;

import com.QuoraUpGrad.quora.service.ServiceConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;


@SpringBootApplication
@Import(ServiceConfiguration.class)
public class QuoraUpGradApplication {
    public static void main(String[] args) {
        SpringApplication.run(QuoraUpGradApplication.class, args);
    }
}