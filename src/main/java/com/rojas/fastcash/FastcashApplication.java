package com.rojas.fastcash;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;

@SpringBootApplication
@EnableScheduling
public class FastcashApplication {

	public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("America/Lima"));
        System.out.println("Configuración de hora JVM establecida a: " + new java.util.Date());

		SpringApplication.run(FastcashApplication.class, args);
	}
}
