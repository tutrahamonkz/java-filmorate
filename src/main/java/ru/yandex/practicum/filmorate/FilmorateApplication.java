package ru.yandex.practicum.filmorate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication // Аннотация, указывающая на то, что это основное приложение Spring Boot
public class FilmorateApplication {
	public static void main(String[] args) {
		SpringApplication.run(FilmorateApplication.class, args); // Запускает приложение Spring Boot
	}
}