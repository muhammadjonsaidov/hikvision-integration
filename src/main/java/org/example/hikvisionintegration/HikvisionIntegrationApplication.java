package org.example.hikvisionintegration;

import org.example.hikvisionintegration.service.HikvisionService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class HikvisionIntegrationApplication {

    public static void main(String[] args) {
        SpringApplication.run(HikvisionIntegrationApplication.class, args);
    }

    // Bu qism dastur ishga tushishi bilan bir marta kamerani sozlash uchun ishlaydi
    @Bean
    public CommandLineRunner configure(HikvisionService hikvisionService) {
        return args -> {
            hikvisionService.configureWebhook();
        };
    }
}
