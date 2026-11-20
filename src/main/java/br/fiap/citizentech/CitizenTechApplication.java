package br.fiap.citizentech;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class CitizenTechApplication {
    public static void main(String[] args) {
        SpringApplication.run(CitizenTechApplication.class, args);
    }
}
