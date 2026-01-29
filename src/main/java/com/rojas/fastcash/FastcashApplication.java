package com.rojas.fastcash;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;

@SpringBootApplication
@EnableScheduling
public class FastcashApplication {

	public static void main(String[] args) {
		SpringApplication.run(FastcashApplication.class, args);
	}

    // ESTO ARREGLA LA HORA: Forza la zona horaria a Perú al iniciar
    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("America/Lima"));
        System.out.println("Configuración de hora establecida a: " + new java.util.Date());
    }
}