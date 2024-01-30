package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);

		System.out.println(
			"""
			----------------------------------------------------
				Application started successfully! Useful URLs:
				Local backend:  	http://localhost:8080/
				Local database: 	http://localhost:8081/
			----------------------------------------------------
			
			""");
	}
}
